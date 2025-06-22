package pl.cuyer.rusthub.presentation.features.auth

import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.usecase.RegisterUserUseCase
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
    val state = _state
        .onStart {
            observeEmail()
            observePassword()
            observeUsername()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )

    var registerJob: Job? = null

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnRegister -> register()
            is RegisterAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is RegisterAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            is RegisterAction.OnUsernameChange -> _state.update { it.copy(username = action.username) }
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

            if (!emailResult.isValid || !passwordResult.isValid || !usernameResult.isValid) return@launch

            registerUserUseCase(email, password, username)
                .onStart {
                    updateLoading(true)
                }
                .catch { e ->
                    showErrorSnackbar(e.message ?: "Unknown error")
                }
                .onCompletion {
                    updateLoading(false)
                }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            Napier.i("Success")
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

    private fun observeEmail() {
        _state
            .map { it.email }
            .distinctUntilChanged()
            .onEach { value ->
                val validation = emailValidator.validate(value)
                _state.update { it.copy(emailError = validation.errorMessage) }
            }
            .launchIn(coroutineScope)
    }

    private fun observePassword() {
        _state
            .map { it.password }
            .distinctUntilChanged()
            .onEach { value ->
                val validation = passwordValidator.validate(value)
                _state.update { it.copy(passwordError = validation.errorMessage) }
            }
            .launchIn(coroutineScope)
    }

    private fun observeUsername() {
        _state
            .map { it.username }
            .distinctUntilChanged()
            .onEach { value ->
                val validation = usernameValidator.validate(value)
                _state.update { it.copy(usernameError = validation.errorMessage) }
            }
            .launchIn(coroutineScope)
    }
}