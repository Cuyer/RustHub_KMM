package pl.cuyer.rusthub.presentation.features.auth.upgrade

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
import pl.cuyer.rusthub.domain.usecase.UpgradeAccountUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.validator.PasswordValidator
import pl.cuyer.rusthub.util.validator.UsernameValidator

class UpgradeViewModel(
    private val upgradeAccountUseCase: UpgradeAccountUseCase,
    private val snackbarController: SnackbarController,
    private val usernameValidator: UsernameValidator,
    private val passwordValidator: PasswordValidator,
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(UpgradeState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = UpgradeState()
    )

    private var submitJob: Job? = null

    fun onAction(action: UpgradeAction) {
        when (action) {
            UpgradeAction.OnSubmit -> submit()
            is UpgradeAction.OnUsernameChange -> updateUsername(action.username)
            is UpgradeAction.OnPasswordChange -> updatePassword(action.password)
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

    private fun submit() {
        submitJob?.cancel()
        submitJob = coroutineScope.launch {
            val usernameResult = usernameValidator.validate(_state.value.username)
            val passwordResult = passwordValidator.validate(_state.value.password)
            _state.update {
                it.copy(
                    usernameError = usernameResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                )
            }
            if (!usernameResult.isValid || !passwordResult.isValid) {
                snackbarController.sendEvent(
                    SnackbarEvent("Please correct the errors above and try again.")
                )
                return@launch
            }
            upgradeAccountUseCase(_state.value.username, _state.value.password)
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e -> showErrorSnackbar(e.message ?: "Unknown error") }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            snackbarController.sendEvent(
                                SnackbarEvent(
                                    "Account has been upgraded successfully!"
                                )
                            )
                            navigateUp()
                        }
                        is Result.Error -> showErrorSnackbar(
                            result.exception.message ?: "Unable to upgrade account"
                        )
                        else -> Unit
                    }
                }
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun showErrorSnackbar(message: String) {
        coroutineScope.launch { snackbarController.sendEvent(SnackbarEvent(message)) }
    }
}
