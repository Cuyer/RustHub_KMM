package pl.cuyer.rusthub.presentation.features.subscription

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.BillingProduct
import pl.cuyer.rusthub.domain.model.BillingErrorCode
import pl.cuyer.rusthub.domain.model.toMessage
import pl.cuyer.rusthub.domain.repository.purchase.BillingRepository
import pl.cuyer.rusthub.domain.usecase.GetActiveSubscriptionUseCase
import pl.cuyer.rusthub.domain.usecase.ConfirmPurchaseUseCase
import pl.cuyer.rusthub.domain.usecase.RefreshUserUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.catchAndLog
import pl.cuyer.rusthub.util.toUserMessage

data class SubscriptionState(
    val products: Map<SubscriptionPlan, BillingProduct> = emptyMap(),
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val currentPlan: SubscriptionPlan? = null,
    val hasError: Boolean = false
)

sealed interface SubscriptionAction {
    data class Subscribe(val plan: SubscriptionPlan, val activity: Any) : SubscriptionAction
}

class SubscriptionViewModel(
    private val billingRepository: BillingRepository,
    private val confirmPurchaseUseCase: ConfirmPurchaseUseCase,
    private val refreshUserUseCase: RefreshUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getActiveSubscriptionUseCase: GetActiveSubscriptionUseCase,
    private val snackbarController: SnackbarController,
    private val stringProvider: StringProvider,
    private val initialPlan: SubscriptionPlan? = null
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(SubscriptionState(currentPlan = initialPlan))
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SubscriptionState(currentPlan = initialPlan)
    )

    private var subscriptionJob: Job? = null

    init {
        observeProducts()
        observePurchases()
        observeErrors()
    }

    fun onAction(action: SubscriptionAction) {
        when (action) {
            is SubscriptionAction.Subscribe -> subscribe(action.plan, action.activity)
        }
    }

    private fun observeProducts() {
        billingRepository.queryProducts(
            SubscriptionPlan.entries.mapNotNull { plan ->
                plan.basePlanId ?: plan.name.let { null }
            } + SubscriptionPlan.LIFETIME.productId
        )
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onEach { list ->
                val map = list.associateBy { it.id }
                _state.update { item ->
                    item.copy(
                        products = SubscriptionPlan.entries
                            .associateWith { plan ->
                                if (plan.basePlanId != null) {
                                    map[plan.basePlanId]
                                } else {
                                    map[plan.productId]
                                }
                            }
                            .filterValues { it != null } as Map<SubscriptionPlan, BillingProduct>,
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false) }
                snackbarController.sendEvent(
                    SnackbarEvent(stringProvider.get(SharedRes.strings.unknown_error))
                )
            }
            .launchIn(coroutineScope)
    }

    private fun observePurchases() {
        billingRepository
            .purchaseFlow
            .distinctUntilChanged()
            .onEach { purchase ->
                CrashReporter.log("Observing purchases flow $purchase")
                CrashReporter.recordException(Exception("Observing purchases flow $purchase"))
                confirmPurchase(purchase.productId, purchase.purchaseToken)
            }.launchIn(coroutineScope)
    }

    private fun observeErrors() {
        billingRepository
            .errorFlow
            .distinctUntilChanged()
            .onEach { code ->
                showErrorSnackbar(code.toMessage(stringProvider))
            }
            .launchIn(coroutineScope)
    }

    private fun subscribe(plan: SubscriptionPlan, activity: Any) {
        coroutineScope.launch {
            if (plan == SubscriptionPlan.LIFETIME && _state.value.currentPlan in listOf(SubscriptionPlan.MONTHLY, SubscriptionPlan.YEARLY)) {
                snackbarController.sendEvent(
                    SnackbarEvent(stringProvider.get(SharedRes.strings.cancel_current_before_lifetime))
                )
                return@launch
            }
            val id = plan.basePlanId ?: plan.productId
            val obfuscated = getUserUseCase().first()?.obfuscatedId
            billingRepository.launchBillingFlow(activity, id, obfuscated)
        }
    }

    private fun confirmPurchase(productId: String, token: String) {
        coroutineScope.launch {
            val plan = SubscriptionPlan.entries.firstOrNull { it.basePlanId == productId || it.productId == productId }
            val id = plan?.takeIf { it.basePlanId == null }?.productId
            confirmPurchaseUseCase(token, id)
                .onStart { _state.update { it.copy(isProcessing = true) } }
                .onCompletion { _state.update { it.copy(isProcessing = false) } }
                .catchAndLog { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> refreshUser()
                        is Result.Error -> showErrorSnackbar(result.exception.toUserMessage(stringProvider))
                    }
                }
        }
    }

    private fun refreshSubscription() {
        subscriptionJob?.cancel()
        subscriptionJob = coroutineScope.launch {
            getActiveSubscriptionUseCase()
                .onStart { _state.update { it.copy(isLoading = true, hasError = false) } }
                .onCompletion { _state.update { it.copy(isLoading = false) } }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> _state.update {
                            it.copy(currentPlan = result.data?.plan, hasError = false)
                        }
                        is Result.Error -> _state.update {
                            it.copy(currentPlan = null, hasError = true)
                        }
                    }
                }
        }
    }

    private suspend fun refreshUser() {
        refreshUserUseCase()
            .catchAndLog { e ->
                showErrorSnackbar(e.toUserMessage(stringProvider))
            }
            .collectLatest { _uiEvent.send(UiEvent.NavigateUp) }
    }

    private suspend fun showErrorSnackbar(message: String?) {
        message ?: return
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
