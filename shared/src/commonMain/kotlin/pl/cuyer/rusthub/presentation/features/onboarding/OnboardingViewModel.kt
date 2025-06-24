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
import pl.cuyer.rusthub.presentation.navigation.Login
import pl.cuyer.rusthub.presentation.navigation.Register
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

class OnboardingViewModel(
    private val authAnonymouslyUseCase: AuthAnonymouslyUseCase,
    private val snackbarController: SnackbarController,
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

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.OnLoginClick -> navigate(Login)
            OnboardingAction.OnRegisterClick -> navigate(Register)
            OnboardingAction.OnContinueAsGuest -> continueAsGuest()
        }
    }

    private fun continueAsGuest() {
        authAnonymouslyJob?.cancel()
        authAnonymouslyJob = coroutineScope.launch {
            authAnonymouslyUseCase()
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e -> showErrorSnackbar(e.message ?: "Unknown error") }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> navigate(ServerList)
                        is Result.Error -> showErrorSnackbar("Error occurred during creating guest account.")
                        else -> Unit
                    }
                }
        }
    }

    private suspend fun showErrorSnackbar(message: String) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun navigate(destination: NavKey) {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(destination))
        }
    }
}
