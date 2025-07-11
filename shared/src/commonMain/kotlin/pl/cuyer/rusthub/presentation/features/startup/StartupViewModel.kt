package pl.cuyer.rusthub.presentation.features.startup

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.ConnectivityException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.usecase.CheckEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.presentation.navigation.ConfirmEmail
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

class StartupViewModel(
    private val snackbarController: SnackbarController,
    private val getUserUseCase: GetUserUseCase,
    private val checkEmailConfirmedUseCase: CheckEmailConfirmedUseCase,
) : BaseViewModel() {

    private val _state = MutableStateFlow(StartupState())
    val state = _state
        .onStart {
            observeUser()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = StartupState()
        )

    private fun observeUser() {
        getUserUseCase()
            .onStart {
                updateLoadingState(true)
            }
            .onEach { user ->
                Napier.i("User: $user")
                if (user != null) {
                    if (user.provider in setOf(AuthProvider.GOOGLE, AuthProvider.ANONYMOUS)) {
                        updateStartDestination(user, true)
                    } else {
                        checkEmailConfirmedUseCase()
                            .catch { showErrorSnackbar(it.message ?: "Unknown error") }
                            .collect { result ->
                                val confirmed = when (result) {
                                    is Result.Success -> result.data
                                    is Result.Error -> {
                                        if (
                                            result.exception is ConnectivityException ||
                                            result.exception is ServiceUnavailableException
                                        ) {
                                            showErrorSnackbar(
                                                "Could not verify e-mail confirmation due to " +
                                                    "connectivity issues."
                                            )
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                    else -> false
                                }
                                updateStartDestination(user, confirmed)
                            }
                    }
                } else {
                    updateStartDestination(null, true)
                }
                updateLoadingState(false)
            }
            .catch {
                showErrorSnackbar("Error occurred during fetching data about the user.")
                updateLoadingState(false)
            }
            .launchIn(coroutineScope)
    }

    private fun updateLoadingState(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }

    private fun updateStartDestination(user: User?, confirmed: Boolean) {
        _state.update {
            it.copy(
                startDestination = when {
                    user == null -> Onboarding
                    confirmed -> ServerList
                    else -> ConfirmEmail
                }
            )
        }
    }

    private fun showErrorSnackbar(message: String) {
        coroutineScope.launch {
            snackbarController.sendEvent(SnackbarEvent(message = message))
        }
    }
}
