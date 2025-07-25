package pl.cuyer.rusthub.presentation.features.auth.confirm

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import pl.cuyer.rusthub.util.catchAndLog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.TooManyRequestsException
import pl.cuyer.rusthub.domain.usecase.CheckEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.ResendConfirmationUseCase
import pl.cuyer.rusthub.domain.usecase.SetEmailConfirmedUseCase
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.toUserMessage

class ConfirmEmailViewModel(
    private val checkEmailConfirmedUseCase: CheckEmailConfirmedUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val resendConfirmationUseCase: ResendConfirmationUseCase,
    private val snackbarController: SnackbarController,
    private val setEmailConfirmedUseCase: SetEmailConfirmedUseCase,
    private val stringProvider: StringProvider,
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ConfirmEmailState())
    val state = _state
        .onStart { observeUser() }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ConfirmEmailState()
        )

    private var confirmJob: Job? = null
    private var resendJob: Job? = null


    fun onAction(action: ConfirmEmailAction) {
        when (action) {
            ConfirmEmailAction.OnConfirm -> confirm()
            ConfirmEmailAction.OnResend -> resend()
            ConfirmEmailAction.OnBack -> navigateBack()
        }
    }

    private fun observeUser() {
        getUserUseCase()
            .distinctUntilChanged()
            .onEach { user ->
                _state.update { it.copy(email = user?.email ?: "", provider = user?.provider) }
            }
            .launchIn(coroutineScope)
    }

    private fun confirm() {
        confirmJob?.cancel()
        confirmJob = coroutineScope.launch {
            checkEmailConfirmedUseCase()
                .onStart { updateConfirmLoading(true) }
                .onCompletion { updateConfirmLoading(false) }
                .catchAndLog { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data) {
                                setEmailConfirmedUseCase(true)
                                _uiEvent.send(UiEvent.Navigate(ServerList))
                            } else {
                                showErrorSnackbar(
                                    stringProvider.get(SharedRes.strings.email_not_confirmed_yet)
                                )
                            }
                        }
                        is Result.Error -> showErrorSnackbar(
                            result.exception.toUserMessage(stringProvider)
                        )
                    }
                }
        }
    }

    private fun resend() {
        resendJob?.cancel()
        resendJob = coroutineScope.launch {
            resendConfirmationUseCase()
                .onStart { updateResendLoading(true) }
                .onCompletion { updateResendLoading(false) }
                .catchAndLog { e -> showErrorSnackbar(e.toUserMessage(stringProvider)) }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> snackbarController.sendEvent(
                            SnackbarEvent(
                                stringProvider.get(SharedRes.strings.confirmation_email_sent)
                            )
                        )
                        is Result.Error -> showErrorSnackbar(result.exception.toUserMessage(stringProvider))
                    }
                }
        }
    }


    private fun handleError(throwable: Throwable): String? {
        return throwable.toUserMessage(stringProvider)
    }

    private fun updateConfirmLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun updateResendLoading(isLoading: Boolean) {
        _state.update { it.copy(resendLoading = isLoading) }
    }

    private suspend fun showErrorSnackbar(message: String?) {
        message ?: return
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }

    private fun navigateBack() {
        coroutineScope.launch { _uiEvent.send(UiEvent.NavigateUp) }
    }
}
