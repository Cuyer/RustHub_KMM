package pl.cuyer.rusthub.presentation.features.auth.upgrade

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import pl.cuyer.rusthub.util.catchAndLog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.usecase.GetGoogleClientIdUseCase
import pl.cuyer.rusthub.domain.usecase.UpgradeAccountUseCase
import pl.cuyer.rusthub.domain.usecase.UpgradeWithGoogleUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.util.RemoteConfig
import pl.cuyer.rusthub.util.RemoteConfigKeys
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.toUserMessage
import pl.cuyer.rusthub.util.validator.EmailValidator
import pl.cuyer.rusthub.util.validator.PasswordValidator
import pl.cuyer.rusthub.util.validator.UsernameValidator

class UpgradeViewModel(
    private val upgradeAccountUseCase: UpgradeAccountUseCase,
    private val upgradeWithGoogleUseCase: UpgradeWithGoogleUseCase,
    private val getGoogleClientIdUseCase: GetGoogleClientIdUseCase,
    private val googleAuthClient: GoogleAuthClient,
    private val snackbarController: SnackbarController,
    private val usernameValidator: UsernameValidator,
    private val passwordValidator: PasswordValidator,
    private val emailValidator: EmailValidator,
    private val stringProvider: StringProvider,
    private val remoteConfig: RemoteConfig,
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(UpgradeState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = _state.value
    )

    private var submitJob: Job? = null
    private var googleJob: Job? = null

    fun onAction(action: UpgradeAction) {
        when (action) {
            UpgradeAction.OnSubmit -> submit()
            is UpgradeAction.OnUsernameChange -> updateUsername(action.username)
            is UpgradeAction.OnPasswordChange -> updatePassword(action.password)
            is UpgradeAction.OnEmailChange -> updateEmail(action.email)
            UpgradeAction.OnGoogleLogin -> startGoogleLogin()
            UpgradeAction.OnNavigateUp -> navigateUp()
        }
    }

    private fun navigateUp() {
        coroutineScope.launch { _uiEvent.send(UiEvent.NavigateUp) }
    }

    private fun updateUsername(username: String) {
        _state.update { it.copy(username = username, usernameError = null) }
    }

    private fun updatePassword(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    private fun updateEmail(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    private fun submit() {
        submitJob?.cancel()
        submitJob = coroutineScope.launch {
            val usernameResult = usernameValidator.validate(_state.value.username)
            val passwordResult = passwordValidator.validate(_state.value.password)
            val emailResult = emailValidator.validate(_state.value.email)
            _state.update {
                it.copy(
                    usernameError = usernameResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                    emailError = emailResult.errorMessage,
                )
            }
            if (!usernameResult.isValid || !passwordResult.isValid || !emailResult.isValid) {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        stringProvider.get(SharedRes.strings.correct_errors_try_again)
                    )
                )
                return@launch
            }
            upgradeAccountUseCase(_state.value.username, _state.value.email, _state.value.password)
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catchAndLog { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> {
                            snackbarController.sendEvent(
                                SnackbarEvent(
                                    stringProvider.get(SharedRes.strings.account_upgraded_successfully)
                                )
                            )
                            _uiEvent.send(UiEvent.NavigateUp)
                        }
                        is Result.Error -> showErrorSnackbar(
                            result.exception.toUserMessage(stringProvider)
                        )
                    }
                }
        }
    }

    private fun startGoogleLogin() {
        googleJob?.cancel()
        googleJob = coroutineScope.launch {
            if (!remoteConfig.getBoolean(RemoteConfigKeys.GOOGLE_AUTH_ENABLED)) {
                showErrorSnackbar(
                    stringProvider.get(SharedRes.strings.google_sign_in_disabled)
                )
                return@launch
            }
            getGoogleClientIdUseCase()
                .onStart { updateGoogleLoading(true) }
                .onCompletion { updateGoogleLoading(false) }
                .catchAndLog { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> {
                            val token = googleAuthClient.getIdToken(result.data)
                            if (token != null) {
                                upgradeWithGoogleToken(token)
                            } else {
                                showErrorSnackbar(
                                    stringProvider.get(SharedRes.strings.google_sign_in_failed)
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

    private fun upgradeWithGoogleToken(token: String) {
        googleJob?.cancel()
        googleJob = coroutineScope.launch {
            upgradeWithGoogleUseCase(token)
                .onStart { updateGoogleLoading(true) }
                .onCompletion { updateGoogleLoading(false) }
                .catchAndLog { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> {
                            snackbarController.sendEvent(
                                SnackbarEvent(
                                    stringProvider.get(SharedRes.strings.account_upgraded_successfully)
                                )
                            )
                            navigateUp()
                        }
                        is Result.Error -> showErrorSnackbar(
                            result.exception.toUserMessage(stringProvider)
                        )
                    }
                }
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun updateGoogleLoading(isLoading: Boolean) {
        _state.update { it.copy(googleLoading = isLoading) }
    }

    private fun showErrorSnackbar(message: String?) {
        message ?: return
        coroutineScope.launch { snackbarController.sendEvent(SnackbarEvent(message)) }
    }
}
