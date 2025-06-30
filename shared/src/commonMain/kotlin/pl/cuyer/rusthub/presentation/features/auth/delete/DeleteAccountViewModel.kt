package pl.cuyer.rusthub.presentation.features.auth.delete

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
import pl.cuyer.rusthub.domain.usecase.DeleteAccountUseCase
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.validator.PasswordValidator
import pl.cuyer.rusthub.util.validator.UsernameValidator

class DeleteAccountViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val snackbarController: SnackbarController,
    private val passwordValidator: PasswordValidator,
    private val usernameValidator: UsernameValidator
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(DeleteAccountState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DeleteAccountState()
    )

    var deleteJob: Job? = null

    fun onAction(action: DeleteAccountAction) {
        when (action) {
            DeleteAccountAction.OnDelete -> delete()
            is DeleteAccountAction.OnPasswordChange -> updatePassword(action.password)
            is DeleteAccountAction.OnUsernameChange -> updateUsername(action.username)
        }
    }

    private fun updatePassword(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    private fun updateUsername(username: String) {
        _state.update { it.copy(username = username, usernameError = null) }
    }

    private fun delete() {
        deleteJob?.cancel()
        deleteJob = coroutineScope.launch {
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
                    SnackbarEvent(message = "Please correct the errors above and try again.")
                )
                return@launch
            }
            deleteAccountUseCase(username, password)
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e -> showErrorSnackbar(e.message ?: "Unknown error") }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> _uiEvent.send(UiEvent.Navigate(Onboarding))
                        is Result.Error -> showErrorSnackbar(result.exception.message ?: "Unable to delete account")
                        else -> Unit
                    }
                }
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private suspend fun showErrorSnackbar(message: String) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
