package pl.cuyer.rusthub.presentation.features.onboarding

import androidx.navigation3.runtime.NavKey
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
import pl.cuyer.rusthub.domain.usecase.AuthAnonymouslyUseCase
import pl.cuyer.rusthub.domain.usecase.CheckUserExistsUseCase
import pl.cuyer.rusthub.domain.usecase.GetGoogleClientIdUseCase
import pl.cuyer.rusthub.domain.usecase.LoginWithGoogleUseCase
import pl.cuyer.rusthub.presentation.navigation.Credentials
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.validator.EmailValidator

class OnboardingViewModel(
    private val authAnonymouslyUseCase: AuthAnonymouslyUseCase,
    private val checkUserExistsUseCase: CheckUserExistsUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val getGoogleClientIdUseCase: GetGoogleClientIdUseCase,
    private val googleAuthClient: GoogleAuthClient,
    private val snackbarController: SnackbarController,
    private val emailValidator: EmailValidator,
    private val stringProvider: StringProvider
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = OnboardingState()
    )

    var authAnonymouslyJob: Job? = null
    var checkEmailJob: Job? = null
    var googleJob: Job? = null

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.OnContinueAsGuest -> continueAsGuest()
            is OnboardingAction.OnEmailChange -> updateEmail(action.email)
            OnboardingAction.OnContinueWithEmail -> continueWithEmail()
            OnboardingAction.OnGoogleLogin -> startGoogleLogin()
            OnboardingAction.OnShowOtherOptions -> toggleOtherOptions()
        }
    }

    private fun continueAsGuest() {
        authAnonymouslyJob?.cancel()
        authAnonymouslyJob = coroutineScope.launch {
            authAnonymouslyUseCase()
                .onStart { updateContinueAsGuestLoading(true) }
                .onCompletion { updateContinueAsGuestLoading(false) }
                .catch { e ->
                    showErrorSnackbar(
                        e.message ?: stringProvider.get(SharedRes.strings.error_unknown)
                    )
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> navigate(ServerList)
                        is Result.Error -> showErrorSnackbar(
                            stringProvider.get(SharedRes.strings.error_creating_guest_account)
                        )
                        else -> Unit
                    }
                }
        }
    }

    private fun updateEmail(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    private fun continueWithEmail() {
        checkEmailJob?.cancel()
        checkEmailJob = coroutineScope.launch {
            val email = _state.value.email
            val emailResult = emailValidator.validate(email)
            _state.update {
                it.copy(
                    emailError = emailResult.errorMessage
                )
            }

            if (!emailResult.isValid) {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        message = stringProvider.get(SharedRes.strings.correct_errors_try_again),
                        action = null
                    )
                )
                return@launch
            }

            checkUserExistsUseCase(email)
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e ->
                    showErrorSnackbar(
                        e.message ?: stringProvider.get(SharedRes.strings.error_unknown)
                    )
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> navigate(
                            Credentials(email, result.data.exists, result.data.provider)
                        )
                        is Result.Error -> showErrorSnackbar(
                            result.exception.message ?: stringProvider.get(SharedRes.strings.error_unknown)
                        )
                        else -> Unit
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
                    showErrorSnackbar(
                        e.message ?: stringProvider.get(SharedRes.strings.error_unknown)
                    )
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
                        is Result.Error -> showErrorSnackbar(
                            result.exception.message
                                ?: stringProvider.get(SharedRes.strings.unable_to_get_client_id)
                        )
                        else -> Unit
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
                    showErrorSnackbar(
                        e.message ?: stringProvider.get(SharedRes.strings.error_unknown)
                    )
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> navigate(ServerList)
                        is Result.Error -> showErrorSnackbar(
                            stringProvider.get(SharedRes.strings.error_google_sign_in)
                        )
                        else -> Unit
                    }
                }
        }
    }

    private fun toggleOtherOptions() {
        _state.update { it.copy(showOtherOptions = !it.showOtherOptions) }
    }

    private suspend fun showErrorSnackbar(message: String) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun updateGoogleLoading(isLoading: Boolean) {
        _state.update { it.copy(googleLoading = isLoading) }
    }

    private fun updateContinueAsGuestLoading(isLoading: Boolean) {
        _state.update { it.copy(continueAsGuestLoading = isLoading) }
    }

    private fun navigate(destination: NavKey) {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(destination))
        }
    }
}
