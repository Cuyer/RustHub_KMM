package pl.cuyer.rusthub.presentation.features.startup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

class StartupViewModel(
    private val snackbarController: SnackbarController,
    private val getUserUseCase: GetUserUseCase,
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
                updateStartDestination(user)
                updateLoadingState(false)
            }
            .catch { e -> showErrorSnackbar("Error occurred during fetching data about the user.") }
            .launchIn(coroutineScope)
    }

    private fun updateLoadingState(loading: Boolean) {
        _state.update {
            it.copy(
                isLoading = loading
            )
        }
    }

    private fun updateStartDestination(user: User?) {
        _state.update {
            it.copy(
                startDestination = if (user != null) ServerList else Onboarding,
            )
        }
    }

    private fun showErrorSnackbar(message: String) {
        coroutineScope.launch {
            snackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message
                )
            )
        }
    }
}
