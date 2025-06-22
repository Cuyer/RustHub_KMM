package pl.cuyer.rusthub.presentation.features.auth

import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.presentation.navigation.UiEvent

class RegisterViewModel : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = RegisterState()
    )

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnRegister -> register(action.email, action.password, action.username)
        }
    }

    private fun register(email: String, password: String, username: String) {
        Napier.i("Registering user with email: $email, password: $password, username: $username")
    }
}