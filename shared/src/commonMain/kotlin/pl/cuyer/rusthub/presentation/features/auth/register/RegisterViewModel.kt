package pl.cuyer.rusthub.presentation.features.auth.register

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.ensureActive
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
import pl.cuyer.rusthub.domain.exception.UserAlreadyExistsException
import pl.cuyer.rusthub.domain.usecase.RegisterUserUseCase
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.validator.EmailValidator
import pl.cuyer.rusthub.util.validator.PasswordValidator
import pl.cuyer.rusthub.util.validator.UsernameValidator

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val snackbarController: SnackbarController,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val usernameValidator: UsernameValidator
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )

    var registerJob: Job? = null

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnRegister -> register()
            is RegisterAction.OnEmailChange -> updateEmail(action.email)

            is RegisterAction.OnPasswordChange -> updatePassword(action.password)

            is RegisterAction.OnUsernameChange -> updateUsername(action.username)

        }
    }

    private fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                emailError = null
            )
        }
    }

    private fun updatePassword(password: String) {
        _state.update {
            it.copy(
                password = password,
                passwordError = null
            )
        }
    }

    private fun updateUsername(username: String) {
        _state.update {
            it.copy(
                username = username,
                usernameError = null
            )
        }
    }

    private fun register() {
        registerJob?.cancel()

        registerJob = coroutineScope.launch {
            val email = _state.value.email
            val password = _state.value.password
            val username = _state.value.username
            val emailResult = emailValidator.validate(email)
            val passwordResult = passwordValidator.validate(password)
            val usernameResult = usernameValidator.validate(username)

            _state.update {
                it.copy(
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                    usernameError = usernameResult.errorMessage
                )
            }

            if (!emailResult.isValid || !passwordResult.isValid || !usernameResult.isValid) {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        message = "Please correct the errors above and try again.",
                        action = null
                    )
                )
                return@launch
            }

            registerUserUseCase(email, password, username)
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e -> showErrorSnackbar(e.message ?: "Unknown error") }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> _uiEvent.send(UiEvent.Navigate(ServerList))

                        is Result.Error ->  when(result.exception) {
                            is UserAlreadyExistsException -> showErrorSnackbar("User already exists.")
                            else -> showErrorSnackbar("Error occurred during creating user account")
                        }

                        else -> Unit
                    }
                }
        }
    }

    private suspend fun showErrorSnackbar(message: String) {
        snackbarController.sendEvent(
            SnackbarEvent(
                message = message,
                action = null
            )
        )
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }
}