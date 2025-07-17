package pl.cuyer.rusthub.presentation.features.auth.password

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
import pl.cuyer.rusthub.domain.usecase.ChangePasswordUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.validator.PasswordValidator

class ChangePasswordViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val snackbarController: SnackbarController,
    private val passwordValidator: PasswordValidator,
    private val stringProvider: StringProvider,
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ChangePasswordState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ChangePasswordState()
    )

    private var changeJob: Job? = null

    fun onAction(action: ChangePasswordAction) {
        when (action) {
            ChangePasswordAction.OnChange -> change()
            is ChangePasswordAction.OnOldPasswordChange -> updateOldPassword(action.password)
            is ChangePasswordAction.OnNewPasswordChange -> updateNewPassword(action.password)
        }
    }

    private fun updateOldPassword(password: String) {
        _state.update { it.copy(oldPassword = password, oldPasswordError = null) }
    }

    private fun updateNewPassword(password: String) {
        _state.update { it.copy(newPassword = password, newPasswordError = null) }
    }

    private fun change() {
        changeJob?.cancel()
        changeJob = coroutineScope.launch {
            val oldPassword = _state.value.oldPassword
            val newPassword = _state.value.newPassword
            val oldResult = passwordValidator.validate(oldPassword)
            val newResult = passwordValidator.validate(newPassword)
            _state.update {
                it.copy(
                    oldPasswordError = oldResult.errorMessage,
                    newPasswordError = newResult.errorMessage
                )
            }
            if (!oldResult.isValid || !newResult.isValid) {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        message = stringProvider.get(SharedRes.strings.correct_errors_try_again)
                    )
                )
                return@launch
            }
            changePasswordUseCase(oldPassword, newPassword)
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e ->
                    showErrorSnackbar(
                        e.message ?: stringProvider.get(SharedRes.strings.error_unknown)
                    )
                }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            snackbarController.sendEvent(
                                SnackbarEvent(
                                    message = stringProvider.get(SharedRes.strings.password_changed_successfully)
                                )
                            )
                            _uiEvent.send(UiEvent.NavigateUp)
                        }
                        is Result.Error -> showErrorSnackbar(
                            result.exception.message
                                ?: stringProvider.get(SharedRes.strings.unable_to_change_password)
                        )
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
