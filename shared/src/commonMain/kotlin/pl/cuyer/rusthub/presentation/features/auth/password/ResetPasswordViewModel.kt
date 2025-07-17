package pl.cuyer.rusthub.presentation.features.auth.password

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.usecase.RequestPasswordResetUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.validator.EmailValidator

class ResetPasswordViewModel(
    email: String,
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase,
    private val snackbarController: SnackbarController,
    private val emailValidator: EmailValidator,
    private val stringProvider: StringProvider,
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ResetPasswordState(email = email))
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ResetPasswordState(email = email)
    )

    private var sendJob: Job? = null

    fun onAction(action: ResetPasswordAction) {
        when (action) {
            ResetPasswordAction.OnSend -> send()
            is ResetPasswordAction.OnEmailChange -> updateEmail(action.email)
        }
    }

    private fun updateEmail(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    private fun send() {
        sendJob?.cancel()
        sendJob = coroutineScope.launch {
            val email = _state.value.email
            val emailResult = emailValidator.validate(email)
            _state.update { it.copy(emailError = emailResult.errorMessage) }
            if (!emailResult.isValid) {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        stringProvider.get(SharedRes.strings.correct_errors_try_again)
                    )
                )
                return@launch
            }
            requestPasswordResetUseCase(email)
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e ->
                    showErrorSnackbar(
                        e.message ?: stringProvider.get(SharedRes.strings.error_unknown)
                    )
                }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            snackbarController.sendEvent(
                                SnackbarEvent(
                                    stringProvider.get(SharedRes.strings.reset_email_sent)
                                )
                            )
                            _uiEvent.send(UiEvent.NavigateUp)
                        }
                        is Result.Error -> showErrorSnackbar(
                            result.exception.message
                                ?: stringProvider.get(SharedRes.strings.unable_to_send_reset_email)
                        )
                    }
                }
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private suspend fun showErrorSnackbar(message: String) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
