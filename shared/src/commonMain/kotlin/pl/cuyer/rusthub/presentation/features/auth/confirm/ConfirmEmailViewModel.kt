package pl.cuyer.rusthub.presentation.features.auth.confirm

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.launchIn
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.usecase.CheckEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.ResendConfirmationUseCase
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.domain.exception.TooManyRequestsException

class ConfirmEmailViewModel(
    private val checkEmailConfirmedUseCase: CheckEmailConfirmedUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val resendConfirmationUseCase: ResendConfirmationUseCase,
    private val snackbarController: SnackbarController,
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
        }
    }

    private fun observeUser() {
        getUserUseCase()
            .onEach { user ->
                _state.update { it.copy(email = user?.email ?: "", provider = user?.provider) }
            }
            .launchIn(coroutineScope)
    }

    private fun confirm() {
        confirmJob?.cancel()
        confirmJob = coroutineScope.launch {
            checkEmailConfirmedUseCase()
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e -> showErrorSnackbar(e.message ?: "Unknown error") }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data) {
                                _uiEvent.send(UiEvent.Navigate(ServerList))
                            } else {
                                showErrorSnackbar("Email not confirmed yet")
                            }
                        }
                        is Result.Error -> showErrorSnackbar(result.exception.message ?: "Unable to verify email")
                        else -> Unit
                    }
                }
        }
    }

    private fun resend() {
        resendJob?.cancel()
        resendJob = coroutineScope.launch {
            resendConfirmationUseCase()
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e -> showErrorSnackbar(handleError(e)) }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> snackbarController.sendEvent(
                            SnackbarEvent("Confirmation email sent")
                        )
                        is Result.Error -> showErrorSnackbar(handleError(result.exception))
                        else -> Unit
                    }
                }
        }
    }

    private fun handleError(throwable: Throwable): String {
        return when (throwable) {
            is TooManyRequestsException -> "Please wait before resending"
            else -> throwable.message ?: "Unknown error"
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private suspend fun showErrorSnackbar(message: String) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
