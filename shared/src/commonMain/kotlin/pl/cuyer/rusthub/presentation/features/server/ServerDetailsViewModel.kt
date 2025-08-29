package pl.cuyer.rusthub.presentation.features.server

import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import pl.cuyer.rusthub.util.catchAndLog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
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
import pl.cuyer.rusthub.domain.usecase.ToggleActionResult
import pl.cuyer.rusthub.domain.exception.FavoriteLimitException
import pl.cuyer.rusthub.domain.exception.SubscriptionLimitException
import pl.cuyer.rusthub.domain.usecase.GetServerDetailsUseCase
import pl.cuyer.rusthub.domain.usecase.ToggleFavouriteUseCase
import pl.cuyer.rusthub.domain.usecase.ToggleSubscriptionUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.ResendConfirmationUseCase
import pl.cuyer.rusthub.presentation.model.ServerInfoUi
import pl.cuyer.rusthub.domain.model.toUiModel
import pl.cuyer.rusthub.presentation.navigation.Subscription
import pl.cuyer.rusthub.presentation.navigation.ConfirmEmail
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.util.ClipboardHandler
import pl.cuyer.rusthub.util.ShareHandler
import pl.cuyer.rusthub.util.ReviewRequester
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.ConnectivityObserver
import pl.cuyer.rusthub.util.toUserMessage
import pl.cuyer.rusthub.SharedRes

class ServerDetailsViewModel(
    private val clipboardHandler: ClipboardHandler,
    private val snackbarController: SnackbarController,
    private val shareHandler: ShareHandler,
    private val reviewRequester: ReviewRequester,
    private val getServerDetailsUseCase: GetServerDetailsUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    private val toggleSubscriptionUseCase: ToggleSubscriptionUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val resendConfirmationUseCase: ResendConfirmationUseCase,
    private val permissionsController: PermissionsController,
    private val stringProvider: StringProvider,
    private val serverName: String?,
    private val serverId: Long?,
    private val connectivityObserver: ConnectivityObserver
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ServerDetailsState())
    val state = _state
        .onStart {
            assignInitialData()
            assignInitialServerDetailsJob()
            observeUser()
            observeConnectivity()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value
        )

    private var toggleJob: Job? = null
    private var subscriptionJob: Job? = null
    private var serverDetailsJob: Job? = null
    private var emailConfirmed: Boolean = true

    fun onAction(action: ServerDetailsAction) {
        when (action) {
            is ServerDetailsAction.OnSaveToClipboard -> saveIpToClipboard(action.ipAddress)
            ServerDetailsAction.OnToggleFavourite -> toggleFavourite()
            ServerDetailsAction.OnDismissSubscriptionDialog -> Unit
            ServerDetailsAction.OnDismissNotificationInfo -> showNotificationInfo(false)
            ServerDetailsAction.OnSubscribe -> handleSubscribeAction()
            ServerDetailsAction.OnShare -> shareServer()
            ServerDetailsAction.OnShowMap -> showMapDialog(true)
            ServerDetailsAction.OnDismissMap -> showMapDialog(false)
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

    private fun observeUser() {
        getUserUseCase()
            .distinctUntilChanged()
            .onEach { user -> emailConfirmed = user?.emailConfirmed == true }
            .launchIn(coroutineScope)
    }

    private fun saveIpToClipboard(ipAddress: String) {
        clipboardHandler.copyToClipboard(
            stringProvider.get(SharedRes.strings.server_address),
            "client.connect $ipAddress"
        )
        coroutineScope.launch {
            snackbarController.sendEvent(
                event = SnackbarEvent(
                    message = stringProvider.get(
                        SharedRes.strings.saved_to_clipboard,
                        ipAddress
                    ),
                    duration = Duration.SHORT
                )
            )
        }
    }

    private fun shareServer() {
        val ip = state.value.details?.serverIp ?: return
        shareHandler.share("client.connect $ip")
    }


    private fun toggleFavourite() {
        val id = state.value.serverId ?: return
        val details = state.value.details ?: return
        val add = details.isFavorite != true

        if (!emailConfirmed) {
            showUnconfirmedSnackbar()
            return
        }

        toggleJob?.cancel()

        toggleJob = coroutineScope.launch {
            toggleFavouriteUseCase(id, add)
                .catchAndLog {
                    showErrorSnackbar(
                        stringProvider.get(
                            if (add) SharedRes.strings.error_add_favourite
                                else SharedRes.strings.error_remove_favourite
                        )
                    )
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is ToggleActionResult.Success, is ToggleActionResult.Queued -> {
                            serverDetailsJob = observeServerDetails(id)
                            snackbarController.sendEvent(
                                event = SnackbarEvent(
                                    message = if (add) {
                                        stringProvider.get(
                                            SharedRes.strings.added_to_favourites,
                                            state.value.serverName ?: ""
                                        )
                                    } else {
                                        stringProvider.get(
                                            SharedRes.strings.removed_from_favourites,
                                            state.value.serverName ?: ""
                                        )
                                    },
                                    duration = Duration.SHORT
                                )
                            )
                            if (result is ToggleActionResult.Queued) {
                                snackbarController.sendEvent(
                                    SnackbarEvent(
                                        message = stringProvider.get(SharedRes.strings.will_sync_when_online),
                                        duration = Duration.SHORT
                                    )
                                )
                            }
                            if (add) {
                                reviewRequester.requestReview()
                            }
                        }
                        is ToggleActionResult.Error -> when (result.exception) {
                            is FavoriteLimitException -> navigateSubscription()
                            else -> showErrorSnackbar(
                                stringProvider.get(
                                    if (add) SharedRes.strings.error_add_favourite
                                    else SharedRes.strings.error_remove_favourite
                                )
                            )
                        }
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
                .catchAndLog {
                    showErrorSnackbar(
                        stringProvider.get(
                            if (subscribed) SharedRes.strings.error_subscribe_notifications
                            else SharedRes.strings.error_unsubscribe_notifications
                        )
                    )
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is ToggleActionResult.Success, is ToggleActionResult.Queued -> {
                            serverDetailsJob = observeServerDetails(id)
                            snackbarController.sendEvent(
                                SnackbarEvent(
                                    message = if (subscribed) {
                                        stringProvider.get(SharedRes.strings.subscribed_to_notifications)
                                    } else {
                                        stringProvider.get(SharedRes.strings.unsubscribed_from_notifications)
                                    },
                                    duration = Duration.SHORT
                                )
                            )
                            if (result is ToggleActionResult.Queued) {
                                snackbarController.sendEvent(
                                    SnackbarEvent(
                                        message = stringProvider.get(SharedRes.strings.will_sync_when_online),
                                        duration = Duration.SHORT
                                    )
                                )
                            }
                            if (subscribed) {
                                reviewRequester.requestReview()
                            }
                        }
                        is ToggleActionResult.Error -> when (result.exception) {
                            is SubscriptionLimitException -> navigateSubscription()
                            else -> showErrorSnackbar(
                                stringProvider.get(
                                    if (subscribed) SharedRes.strings.error_subscribe_notifications
                                    else SharedRes.strings.error_unsubscribe_notifications
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun navigateSubscription() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(Subscription()))
        }
    }

    private fun navigateConfirmEmail() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(ConfirmEmail))
        }
    }

    private fun showNotificationInfo(show: Boolean) {
        _state.update {
            it.copy(
                showNotificationInfo = show
            )
        }
    }

    private fun showMapDialog(show: Boolean) {
        _state.update {
            it.copy(
                showMap = show
            )
        }
    }

    private fun handleSubscribeAction() {
        coroutineScope.launch {
            if (!emailConfirmed) {
                showUnconfirmedSnackbar()
                return@launch
            }
            val details = state.value.details
            if (details?.nextWipe == null && details?.nextMapWipe == null) {
                snackbarController.sendEvent(
                    SnackbarEvent(stringProvider.get(SharedRes.strings.no_wipe_for_subscription))
                )
                return@launch
            }
            try {
                permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
                toggleSubscription()
            } catch (_: DeniedAlwaysException) {
                permissionsController.openAppSettings()
            } catch (_: DeniedException) {
                showNotificationInfo(true)
            }
        }
    }

    private suspend fun showErrorSnackbar(message: String?) {
        message ?: return
        snackbarController.sendEvent(
            SnackbarEvent(message = message, action = null)
        )
    }

    private fun showUnconfirmedSnackbar() {
        coroutineScope.launch {
            snackbarController.sendEvent(
                SnackbarEvent(
                    message = stringProvider.get(SharedRes.strings.email_not_confirmed),
                    action = SnackbarAction(
                        stringProvider.get(SharedRes.strings.resend)
                    ) { navigateConfirmEmail() }
                )
            )
        }
    }

    private fun resendConfirmation() {
        coroutineScope.launch {
            resendConfirmationUseCase()
                .catchAndLog { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> snackbarController.sendEvent(
                            SnackbarEvent(
                                stringProvider.get(SharedRes.strings.confirmation_email_sent)
                            )
                        )
                        is Result.Error -> showErrorSnackbar(
                            result.exception.toUserMessage(stringProvider)
                        )
                    }
                }
        }
    }


    private fun observeServerDetails(serverId: Long): Job {
        serverDetailsJob?.cancel()
        return getServerDetailsUseCase(serverId)
            .map { it?.toUiModel(stringProvider) }
            .flowOn(Dispatchers.Default)
            .onEach { mappedDetails ->
                updateDetails(mappedDetails)
                changeIsLoading(false)
            }
            .onStart { changeIsLoading(true) }
            .catchAndLog { e ->
                showErrorSnackbar(
                    e.toUserMessage(stringProvider)
                        ?: stringProvider.get(SharedRes.strings.error_fetching_server_data)
                )
            }
            .launchIn(coroutineScope)
    }

    private fun observeConnectivity() {
        connectivityObserver.isConnected
            .onEach { connected ->
                _state.update { it.copy(isConnected = connected) }
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