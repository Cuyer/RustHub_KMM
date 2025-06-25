package pl.cuyer.rusthub.presentation.features.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.FavoriteLimitException
import pl.cuyer.rusthub.domain.exception.SubscriptionLimitException
import pl.cuyer.rusthub.domain.usecase.GetServerDetailsUseCase
import pl.cuyer.rusthub.domain.usecase.ToggleFavouriteUseCase
import pl.cuyer.rusthub.domain.usecase.ToggleSubscriptionUseCase
import pl.cuyer.rusthub.presentation.model.ServerInfoUi
import pl.cuyer.rusthub.presentation.model.toUi
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.ClipboardHandler

class ServerDetailsViewModel(
    private val clipboardHandler: ClipboardHandler,
    private val snackbarController: SnackbarController,
    private val getServerDetailsUseCase: GetServerDetailsUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    private val toggleSubscriptionUseCase: ToggleSubscriptionUseCase,
    private val serverName: String?,
    private val serverId: Long?
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ServerDetailsState())
    val state = _state
        .onStart {
            assignInitialData()
            assignInitialServerDetailsJob()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ServerDetailsState()
        )

    private var toggleJob: Job? = null
    private var subscriptionJob: Job? = null
    private var serverDetailsJob: Job? = null

    fun onAction(action: ServerDetailsAction) {
        when (action) {
            is ServerDetailsAction.OnSaveToClipboard -> saveIpToClipboard(action.ipAddress)
            ServerDetailsAction.OnToggleFavourite -> toggleFavourite()
            ServerDetailsAction.OnDismissSubscriptionDialog -> showSubscriptionDialog(false)
            ServerDetailsAction.OnDismissNotificationDialog -> showNotificationInfoDialog(false)
            ServerDetailsAction.OnSubscribe -> handleSubscribeAction()
        }
    }

    private fun assignInitialData() {
        _state.update {
            it.copy(
                serverId = serverId,
                serverName = serverName,
                details = it.details
            )
        }
    }

    private fun assignInitialServerDetailsJob() {
        serverId?.let {
            serverDetailsJob = observeServerDetails(it)
        }
    }

    private fun saveIpToClipboard(ipAddress: String) {
        clipboardHandler.copyToClipboard("Server address", ipAddress)
        coroutineScope.launch {
            snackbarController.sendEvent(
                event = SnackbarEvent(
                    message = "Saved $ipAddress to the clipboard!",
                    duration = Duration.SHORT
                )
            )
        }
    }


    private fun toggleFavourite() {
        val id = state.value.serverId ?: return
        val details = state.value.details ?: return
        val add = details.isFavorite != true

        toggleJob?.cancel()

        toggleJob = coroutineScope.launch {
            toggleFavouriteUseCase(id, add)
                .catch { e -> showErrorSnackbar("Error occurred when trying to ${if (add) "add" else "remove"} server from favourites") }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> {
                            serverDetailsJob = observeServerDetails(id)
                            snackbarController.sendEvent(
                                event = SnackbarEvent(
                                    message = if (add) "Added ${state.value.serverId} to favourites" else "Removed ${state.value.serverId} from favourites",
                                    duration = Duration.SHORT
                                )
                            )
                        }
                        is Result.Error -> when (result.exception) {
                            is FavoriteLimitException -> showSubscriptionDialog(true)
                            else -> showErrorSnackbar("Error occurred when trying to ${if (add) "add" else "remove"} server from favourites")
                        }
                        Result.Loading -> Unit
                    }
                }
        }
    }

    private fun toggleSubscription() {
        val id = state.value.serverId ?: return
        val details = state.value.details ?: return
        val subscribed = details.isSubscribed != true

        subscriptionJob?.cancel()

        subscriptionJob = coroutineScope.launch {
            toggleSubscriptionUseCase(id, subscribed)
                .catch { e ->
                    showErrorSnackbar("Error occurred when trying to ${if (subscribed) "subscribe" else "unsubscribe"} from notifications")
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> {
                            serverDetailsJob = observeServerDetails(id)
                            snackbarController.sendEvent(
                                SnackbarEvent(
                                    message = if (subscribed) "Subscribed to notifications" else "Unsubscribed from notifications",
                                    duration = Duration.SHORT
                                )
                            )
                        }
                        is Result.Error -> when (result.exception) {
                            is SubscriptionLimitException -> showSubscriptionDialog(true)
                            else -> showErrorSnackbar("Error occurred when trying to ${if (subscribed) "subscribe" else "unsubscribe"} from notifications")
                        }
                        Result.Loading -> Unit
                    }
                }
        }
    }

    private fun showSubscriptionDialog(show: Boolean) {
        _state.update {
            it.copy(
                showSubscriptionDialog = show
            )
        }
    }

    private fun showNotificationInfoDialog(show: Boolean) {
        _state.update {
            it.copy(
                showNotificationInfoDialog = show
            )
        }
    }

    private fun handleSubscribeAction() {
        val subscribed = state.value.details?.isSubscribed == true
        when {
            state.value.showSubscriptionDialog -> {
                coroutineScope.launch {
                    snackbarController.sendEvent(
                        SnackbarEvent(
                            message = "Subscribed to notifications",
                            duration = Duration.SHORT
                        )
                    )
                }
                showSubscriptionDialog(false)
            }
            state.value.showNotificationInfoDialog -> {
                toggleSubscription()
                showNotificationInfoDialog(false)
            }
            !subscribed -> showNotificationInfoDialog(true)
            else -> toggleSubscription()
        }
    }

    private suspend fun showErrorSnackbar(message: String) {
        snackbarController.sendEvent(
            SnackbarEvent(message = message, action = null)
        )
    }


    private fun observeServerDetails(serverId: Long): Job {
        serverDetailsJob?.cancel()
        return getServerDetailsUseCase(serverId)
            .map { it?.toUi() }
            .flowOn(Dispatchers.Default)
            .onEach { mappedDetails ->
                updateDetails(mappedDetails)
                changeIsLoading(false)
            }
            .onStart { changeIsLoading(true) }
            .catch { e ->
                showErrorSnackbar("Error occured when fetching data about the server")
            }
            .launchIn(coroutineScope)
    }

    private fun updateDetails(details: ServerInfoUi?) {
        _state.update {
            it.copy(
                details = details,
            )
        }
    }

    private fun changeIsLoading(loading: Boolean) {
        _state.update {
            it.copy(
                isLoading = loading
            )
        }
    }
}