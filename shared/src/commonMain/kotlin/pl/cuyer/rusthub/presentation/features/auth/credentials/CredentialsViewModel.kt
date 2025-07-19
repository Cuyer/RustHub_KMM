package pl.cuyer.rusthub.presentation.features.auth.credentials

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.InvalidCredentialsException
import pl.cuyer.rusthub.domain.exception.UserAlreadyExistsException
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.usecase.CheckEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.GetGoogleClientIdUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.LoginUserUseCase
import pl.cuyer.rusthub.domain.usecase.LoginWithGoogleUseCase
import pl.cuyer.rusthub.domain.usecase.RegisterUserUseCase
import pl.cuyer.rusthub.presentation.navigation.ResetPassword
import pl.cuyer.rusthub.presentation.navigation.ConfirmEmail
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.toUserMessage
import pl.cuyer.rusthub.util.validator.PasswordValidator
import pl.cuyer.rusthub.util.validator.UsernameValidator
import pl.cuyer.rusthub.util.validator.ValidationResult

class CredentialsViewModel(
    email: String,
    private val userExists: Boolean,
    private val provider: AuthProvider?,
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val checkEmailConfirmedUseCase: CheckEmailConfirmedUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val snackbarController: SnackbarController,
    private val passwordValidator: PasswordValidator,
    private val usernameValidator: UsernameValidator,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val getGoogleClientIdUseCase: GetGoogleClientIdUseCase,
    private val googleAuthClient: GoogleAuthClient,
    private val stringProvider: StringProvider
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(
        CredentialsState(email = email, userExists = userExists, provider = provider)
    )
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CredentialsState(email = email, userExists = userExists, provider = provider)
    )

    private var submitJob: Job? = null
    var googleJob: Job? = null

    fun onAction(action: CredentialsAction) {
        when (action) {
            CredentialsAction.OnSubmit -> submit()
            CredentialsAction.OnGoogleLogin -> startGoogleLogin()
            is CredentialsAction.OnUsernameChange -> updateUsername(action.username)
            is CredentialsAction.OnPasswordChange -> updatePassword(action.password)
            CredentialsAction.OnForgotPassword -> navigateResetPassword()
            CredentialsAction.OnNavigateUp -> navigateUp()
        }
    }

    private fun navigateUp() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.NavigateUp)
        }
    }

    private fun navigateResetPassword() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(ResetPassword(_state.value.email)))
        }
    }

    private fun submit() {
        submitJob?.cancel()
        submitJob = coroutineScope.launch {
            val password = _state.value.password
            val passwordResult = passwordValidator.validate(password)
            var usernameResult = ValidationResult(true)
            if (!userExists) {
                usernameResult = usernameValidator.validate(_state.value.username)
            }
            _state.update { it.copy(passwordError = passwordResult.errorMessage, usernameError = usernameResult.errorMessage) }
            if (!passwordResult.isValid || !usernameResult.isValid) {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        stringProvider.get(SharedRes.strings.correct_errors_try_again)
                    )
                )
                return@launch
            }
            val currentEmail = _state.value.email
            if (userExists) {
                loginUserUseCase(currentEmail, password)
            } else {
                registerUserUseCase(currentEmail, password, _state.value.username)
            }
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> handlePostAuth()
                        is Result.Error -> handleError(result.exception)
                    }
                }
        }
    }

    private fun startGoogleLogin() {
        googleJob?.cancel()
        googleJob = coroutineScope.launch {
            getGoogleClientIdUseCase()
                .onStart { updateGoogleLoading(true) }
                .onCompletion { updateGoogleLoading(false) }
                .catch { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            val token = googleAuthClient.getIdToken(result.data)
                            if (token != null) {
                                loginWithGoogleToken(token)
                            } else {
                                showErrorSnackbar(
                                    stringProvider.get(SharedRes.strings.google_sign_in_failed)
                                )
                            }
                        }
                        is Result.Error -> showErrorSnackbar(result.exception.toUserMessage(stringProvider))
                    }
                }
        }
    }

    private fun loginWithGoogleToken(token: String) {
        googleJob?.cancel()
        googleJob = coroutineScope.launch {
            loginWithGoogleUseCase(token)
                .onStart { updateGoogleLoading(true) }
                .onCompletion { updateGoogleLoading(false) }
                .catch { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> navigate(ServerList)
                        is Result.Error -> showErrorSnackbar(
                            stringProvider.get(SharedRes.strings.error_google_sign_in)
                        )
                    }
                }
        }
    }

    private suspend fun handlePostAuth() {
        val user = getUserUseCase().first()
        if (user?.provider == AuthProvider.LOCAL) {
            if (!userExists) {
                navigate(ConfirmEmail)
                return
            }

            when (val result = checkEmailConfirmedUseCase().first()) {
                is Result.Success -> {
                    if (result.data) {
                        navigate(ServerList)
                    } else {
                        navigate(ConfirmEmail)
                    }
                }

                is Result.Error -> {
                    showErrorSnackbar(
                        result.exception.message
                            ?: stringProvider.get(SharedRes.strings.unable_to_verify_email)
                    )
                }
            }
        } else {
            navigate(ServerList)
        }
    }

    private fun updateGoogleLoading(isLoading: Boolean) {
        _state.update { it.copy(googleLoading = isLoading) }
    }

    private fun handleError(exception: Throwable) {
        showErrorSnackbar(exception.toUserMessage(stringProvider))
    }

    private fun updateUsername(username: String) {
        _state.update { it.copy(username = username, usernameError = null) }
    }

    private fun updatePassword(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun navigate(destination: NavKey) {
        coroutineScope.launch { _uiEvent.send(UiEvent.Navigate(destination)) }
    }

    private fun showErrorSnackbar(message: String?) {
        message ?: return
        coroutineScope.launch { snackbarController.sendEvent(SnackbarEvent(message = message)) }
    }
}
