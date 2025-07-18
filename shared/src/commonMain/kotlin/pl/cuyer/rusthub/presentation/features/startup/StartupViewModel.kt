package pl.cuyer.rusthub.presentation.features.startup

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.ConnectivityException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.model.UserPreferences
import pl.cuyer.rusthub.domain.usecase.GetUserPreferencesUseCase
import pl.cuyer.rusthub.domain.usecase.CheckEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.SetEmailConfirmedUseCase
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

class StartupViewModel(
    private val snackbarController: SnackbarController,
    private val getUserUseCase: GetUserUseCase,
    private val checkEmailConfirmedUseCase: CheckEmailConfirmedUseCase,
    private val setEmailConfirmedUseCase: SetEmailConfirmedUseCase,
    private val stringProvider: StringProvider,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
) : BaseViewModel() {

    private val userFlow = getUserUseCase()
        .distinctUntilChanged()
        .shareIn(coroutineScope, SharingStarted.WhileSubscribed(5_000L), 1)

    private val preferencesFlow = getUserPreferencesUseCase()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = UserPreferences()
        )

    private val _state = MutableStateFlow(StartupState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StartupState()
    )

    init {
        observeUser()
        observePreferences()
        coroutineScope.launch { initialize() }
    }

    private fun observeUser() {
        userFlow
            .onEach { user ->
                updateStartDestination(user)
            }
            .catch {
                showErrorSnackbar(stringProvider.get(SharedRes.strings.fetch_user_error))
            }
            .launchIn(coroutineScope)
    }

    private fun observePreferences() {
        preferencesFlow
            .onEach { prefs ->
                updateTheme(prefs.themeConfig, prefs.useDynamicColor)
            }
            .catch { e ->
                Napier.e("Error reading preferences", e)
            }
            .launchIn(coroutineScope)
    }

    private suspend fun initialize() {
        updateLoadingState(true)
        try {
            val user = userFlow.first()
            if (user != null && user.provider == AuthProvider.LOCAL) {
                when (val result = checkEmailConfirmedUseCase().first()) {
                    is Result.Success -> setEmailConfirmedUseCase(result.data)
                    is Result.Error -> showErrorSnackbar(
                        result.exception.message ?: stringProvider.get(SharedRes.strings.error_unknown)
                    )
                }
            }
            updateStartDestination(user)
        } catch (e: Exception) {
            showErrorSnackbar(stringProvider.get(SharedRes.strings.fetch_user_error))
            updateStartDestination(null)
        } finally {
            updateLoadingState(false)
        }
    }

    private fun updateLoadingState(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }

    private fun updateStartDestination(user: User?) {
        _state.update {
            it.copy(
                startDestination = if (user == null) Onboarding else ServerList
            )
        }
    }

    private fun updateTheme(theme: Theme, dynamicColor: Boolean) {
        _state.update {
            it.copy(theme = theme, dynamicColors = dynamicColor)
        }
    }

    private fun showErrorSnackbar(message: String) {
        coroutineScope.launch {
            snackbarController.sendEvent(SnackbarEvent(message = message))
        }
    }
}