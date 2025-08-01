package pl.cuyer.rusthub.presentation.features.auth.delete

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import pl.cuyer.rusthub.util.catchAndLog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.usecase.DeleteAccountUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.GetActiveSubscriptionUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.common.user.UserEvent
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.toUserMessage
import pl.cuyer.rusthub.util.validator.PasswordValidator

class DeleteAccountViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val snackbarController: SnackbarController,
    private val passwordValidator: PasswordValidator,
    private val getUserUseCase: GetUserUseCase,
    private val getActiveSubscriptionUseCase: GetActiveSubscriptionUseCase,
    private val stringProvider: StringProvider,
    private val userEventController: UserEventController,
) : BaseViewModel() {
    private val _state = MutableStateFlow(DeleteAccountState())
    val state = _state
        .onStart {
            observeUser()
            observeSubscription()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DeleteAccountState()
        )

    var deleteJob: Job? = null

    fun onAction(action: DeleteAccountAction) {
        when (action) {
            DeleteAccountAction.OnDelete -> onDeletePressed()
            DeleteAccountAction.OnConfirmDelete -> delete()
            DeleteAccountAction.OnDismissDialog -> _state.update { it.copy(showSubscriptionDialog = false) }
            is DeleteAccountAction.OnPasswordChange -> updatePassword(action.password)
        }
    }

    private fun updatePassword(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    private fun onDeletePressed() {
        if (_state.value.hasSubscription) {
            _state.update { it.copy(showSubscriptionDialog = true) }
        } else {
            delete()
        }
    }

    private fun observeUser() {
        getUserUseCase()
            .distinctUntilChanged()
            .onEach { user -> _state.update { it.copy(provider = user?.provider) } }
            .launchIn(coroutineScope)
    }

    private fun observeSubscription() {
        getActiveSubscriptionUseCase()
            .onEach { result ->
                val hasSub = result is Result.Success && result.data != null
                _state.update { it.copy(hasSubscription = hasSub) }
            }
            .launchIn(coroutineScope)
    }

    private fun delete() {
        deleteJob?.cancel()
        deleteJob = coroutineScope.launch {
            if (_state.value.provider == AuthProvider.GOOGLE) {
                deleteAccountUseCase("")
                    .onStart { updateLoading(true) }
                    .onCompletion { updateLoading(false) }
                    .catchAndLog { e ->
                        showErrorSnackbar(e.toUserMessage(stringProvider))
                    }
                    .collectLatest { result ->
                        when (result) {
                            is Result.Success -> {
                                snackbarController.sendEvent(
                                    SnackbarEvent(
                                        message = stringProvider.get(SharedRes.strings.account_deleted_successfully)
                                    )
                                )
                                userEventController.sendEvent(UserEvent.LoggedOut)
                            }
                            is Result.Error -> showErrorSnackbar(
                                result.exception.toUserMessage(stringProvider)
                            )
                        }
                    }
                return@launch
            }

            val password = _state.value.password
            val passwordResult = passwordValidator.validate(password)
            _state.update {
                it.copy(
                    passwordError = passwordResult.errorMessage
                )
            }
            if (!passwordResult.isValid) {
                snackbarController.sendEvent(
                    SnackbarEvent(
                        message = stringProvider.get(SharedRes.strings.correct_errors_try_again)
                    )
                )
                return@launch
            }
            deleteAccountUseCase(password)
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catchAndLog { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            snackbarController.sendEvent(
                                SnackbarEvent(
                                    message = stringProvider.get(SharedRes.strings.account_deleted_successfully)
                                )
                            )
                            userEventController.sendEvent(UserEvent.LoggedOut)
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

    private suspend fun showErrorSnackbar(message: String?) {
        message ?: return
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}
