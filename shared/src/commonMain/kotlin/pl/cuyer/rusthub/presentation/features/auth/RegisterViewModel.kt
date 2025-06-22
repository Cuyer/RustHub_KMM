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
import pl.cuyer.rusthub.domain.usecase.RegisterUserUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val snackbarController: SnackbarController
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
            is RegisterAction.OnRegister -> register(action.email, action.password, action.username)
        }
    }

    private fun register(email: String, password: String, username: String) {
        registerJob?.cancel()

        registerJob = coroutineScope.launch {
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
}