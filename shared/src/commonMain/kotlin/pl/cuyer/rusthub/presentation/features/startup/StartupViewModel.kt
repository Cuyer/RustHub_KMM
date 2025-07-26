package pl.cuyer.rusthub.presentation.features.startup

import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import pl.cuyer.rusthub.util.catchAndLog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.model.UserPreferences
import pl.cuyer.rusthub.domain.usecase.GetUserPreferencesUseCase
import pl.cuyer.rusthub.domain.usecase.CheckEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.SetEmailConfirmedUseCase
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.getCurrentAppLanguage
import pl.cuyer.rusthub.util.ItemsScheduler
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource
import pl.cuyer.rusthub.domain.model.ItemSyncState
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource
import pl.cuyer.rusthub.util.PurchaseSyncScheduler

private const val SKIP_DELAY = 10_000L

class StartupViewModel(
    private val snackbarController: SnackbarController,
    private val getUserUseCase: GetUserUseCase,
    private val checkEmailConfirmedUseCase: CheckEmailConfirmedUseCase,
    private val setEmailConfirmedUseCase: SetEmailConfirmedUseCase,
    private val stringProvider: StringProvider,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val itemsScheduler: ItemsScheduler,
    private val itemDataSource: ItemDataSource,
    private val itemSyncDataSource: ItemSyncDataSource,
    private val purchaseSyncDataSource: PurchaseSyncDataSource,
    private val purchaseSyncScheduler: PurchaseSyncScheduler,
) : BaseViewModel() {

    private var startupJob: Job? = null
    private val initializationJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
        initialize()
    }
    private val preferencesFlow = getUserPreferencesUseCase()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = UserPreferences()
        )

    private val _state = MutableStateFlow(StartupState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StartupState()
    )

    init {
        observePreferences()
        startupJob = coroutineScope.launch {
            if (itemDataSource.isEmpty(getCurrentAppLanguage())) {
                updateLoadingState(true)
                itemSyncDataSource.setState(ItemSyncState.PENDING)
                itemsScheduler.startNow()
                startSkipTimer()
                itemSyncDataSource.observeState().first { it == ItemSyncState.DONE }
            } else {
                itemsScheduler.schedule()
            }
            if (purchaseSyncDataSource.getPendingOperations().isNotEmpty()) {
                purchaseSyncScheduler.schedule()
            }
            initializationJob.start()
        }
    }

    private fun observePreferences() {
        preferencesFlow
            .onEach { prefs ->
                val theme = if (prefs.useSystemColors) Theme.SYSTEM else prefs.themeConfig
                updateTheme(theme, prefs.useDynamicColor)
            }
            .catchAndLog { e ->
                Napier.e("Error reading preferences", e)
            }
            .launchIn(coroutineScope)
    }

    private suspend fun initialize() {
        updateLoadingState(true)
        try {
            val user = getUserUseCase().first()
            if (user != null && user.provider == AuthProvider.LOCAL) {
                when (val result = checkEmailConfirmedUseCase().first()) {
                    is Result.Success -> setEmailConfirmedUseCase(result.data)
                    is Result.Error -> Unit
                }
            }
            updateStartDestination(user)
        } catch (e: Exception) {
            CrashReporter.recordException(e)
            showErrorSnackbar(stringProvider.get(SharedRes.strings.fetch_user_error))
            updateStartDestination(null)
        } finally {
            updateLoadingState(false)
        }
    }

    private fun updateLoadingState(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }

    private fun startSkipTimer() {
        coroutineScope.launch {
            delay(SKIP_DELAY)
            _state.update { it.copy(showSkip = true) }
        }
    }

    fun skipFetching() {
        startupJob?.cancel()
        initializationJob.start()
    }

    private fun updateStartDestination(user: User?) {
        _state.update {
            it.copy(
                startDestination = if (user == null) Onboarding else ServerList
            )
        }
    }

    private fun updateTheme(theme: Theme, dynamicColor: Boolean) {
        _state.update {
            it.copy(theme = theme, dynamicColors = dynamicColor)
        }
    }

    private fun showErrorSnackbar(message: String?) {
        message ?: return
        coroutineScope.launch {
            snackbarController.sendEvent(SnackbarEvent(message = message))
        }
    }
}