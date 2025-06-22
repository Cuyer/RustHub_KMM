package pl.cuyer.rusthub.presentation.features.auth

import io.github.aakira.napier.Napier
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
import pl.cuyer.rusthub.domain.usecase.LoginUserUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.validator.PasswordValidator
import pl.cuyer.rusthub.util.validator.UsernameValidator

class LoginViewModel(
    private val loginUserUseCase: LoginUserUseCase,
    private val snackbarController: SnackbarController,
    private val passwordValidator: PasswordValidator,
    private val usernameValidator: UsernameValidator,
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(LoginState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = LoginState()
    )

    var loginJob: Job? = null

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnLogin -> login()
            is LoginAction.OnPasswordChange -> _state.update {
                it.copy(
                    password = action.password,
                    passwordError = null
                )
            }
            is LoginAction.OnUsernameChange -> _state.update {
                it.copy(
                    username = action.username,
                    usernameError = null
                )
            }
        }
    }

    private fun login() {
        loginJob?.cancel()

        loginJob = coroutineScope.launch {
            val username = _state.value.username
            val password = _state.value.password
            val usernameResult = usernameValidator.validate(username)
            val passwordResult = passwordValidator.validate(password)

            _state.update {
                it.copy(
                    usernameError = usernameResult.errorMessage,
                    passwordError = passwordResult.errorMessage
                )
            }

            if (!usernameResult.isValid || !passwordResult.isValid) {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        message = "Please correct the errors above and try again.",
                        action = null
                    )
                )
                return@launch
            }

            loginUserUseCase(username, password)
                .onStart { updateLoading(true) }
                .catch { e -> showErrorSnackbar(e.message ?: "Unknown error") }
                .onCompletion { updateLoading(false) }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            Napier.i("Logged in")
                        }

                        is Result.Error -> {
                            showErrorSnackbar(result.exception.message ?: "Unknown error")
                        }

                        else -> Unit
                    }
                }
        }
    }

    private suspend fun showErrorSnackbar(message: String) {
        snackbarController.sendEvent(
            SnackbarEvent(message = message, action = null)
        )
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }
}
