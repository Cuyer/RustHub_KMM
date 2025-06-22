package pl.cuyer.rusthub.presentation.features.onboarding

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.presentation.navigation.Login
import pl.cuyer.rusthub.presentation.navigation.Register
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.UiEvent

class OnboardingViewModel : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = OnboardingState()
    )

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.OnLoginClick -> navigate(Login)
            OnboardingAction.OnRegisterClick -> navigate(Register)
            OnboardingAction.OnContinueAsGuest -> navigate(ServerList)
        }
    }

    private fun navigate(destination: NavKey) {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(destination))
        }
    }
}
