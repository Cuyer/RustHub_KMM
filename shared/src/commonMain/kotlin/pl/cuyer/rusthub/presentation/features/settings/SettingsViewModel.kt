package pl.cuyer.rusthub.presentation.features.settings

import dev.icerock.moko.permissions.PermissionsController
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.usecase.GetSettingsUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.LogoutUserUseCase
import pl.cuyer.rusthub.domain.usecase.SaveSettingsUseCase
import pl.cuyer.rusthub.presentation.navigation.ChangePassword
import pl.cuyer.rusthub.presentation.navigation.DeleteAccount
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.PrivacyPolicy
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.navigation.UpgradeAccount
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.util.anonymousAccountExpiresIn
import pl.cuyer.rusthub.util.formatExpiration

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val permissionsController: PermissionsController,
    private val googleAuthClient: GoogleAuthClient,
    private val snackbarController: SnackbarController
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private var logoutJob: Job? = null
    private val _state = MutableStateFlow(SettingsState())
    val state = _state
        .onStart {
            observeSettings()
            observeUser()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsState()
        )

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnThemeChange -> updateTheme(action.theme)
            is SettingsAction.OnLanguageChange -> updateLanguage(action.language)
            SettingsAction.OnChangePasswordClick -> navigateChangePassword()
            SettingsAction.OnNotificationsClick -> permissionsController.openAppSettings()
            SettingsAction.OnLogout -> logout()
            SettingsAction.OnSubscriptionClick -> showSubscriptionDialog(true)
            SettingsAction.OnDismissSubscriptionDialog -> showSubscriptionDialog(false)
            SettingsAction.OnSubscribe -> showSubscriptionDialog(false)
            SettingsAction.OnPrivacyPolicy -> openPrivacyPolicy()
            SettingsAction.OnDeleteAccount -> navigateDeleteAccount()
            SettingsAction.OnUpgradeAccount -> navigateUpgrade()
        }
    }

    private fun updateTheme(theme: Theme) {
        _state.update { it.copy(theme = theme) }
        save()
    }

    private fun updateLanguage(language: Language) {
        _state.update { it.copy(language = language) }
        save()
    }

    private fun navigateChangePassword() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(ChangePassword))
        }
    }

    private fun navigateUpgrade() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(UpgradeAccount))
        }
    }

    private fun observeSettings() {
        getSettingsUseCase()
            .onEach { settings ->
                settings?.let { updateFromSettings(it) }
            }.launchIn(coroutineScope)
    }

    private fun observeUser() {
        getUserUseCase()
            .onEach { updateUser(it) }
            .launchIn(coroutineScope)
    }

    private fun updateUser(user: User?) {
        _state.update {
            it.copy(
                username = if (user?.provider == AuthProvider.GOOGLE) user.username.substringBefore(
                    "-"
                ) else user?.username,
                provider = user?.provider,
                subscribed = user?.subscribed == true,
                anonymousExpiration = user?.let { u ->
                    if (u.provider == AuthProvider.ANONYMOUS) {
                        anonymousAccountExpiresIn(u.accessToken)?.let { formatExpiration(it) }
                    } else {
                        null
                    }
                }
            )
        }
    }

    private fun save() {
        val settings = Settings(state.value.theme, state.value.language)
        coroutineScope.launch { saveSettingsUseCase(settings) }
    }

    private fun updateFromSettings(settings: Settings) {
        Napier.d("Update from settings $settings")
        _state.update { it.copy(theme = settings.theme, language = settings.language) }
    }

    private fun logout() {
        logoutJob?.cancel()
        logoutJob = coroutineScope.launch {
            logoutUserUseCase()
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catch { e -> showErrorSnackbar(e.message ?: "Unknown error") }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            if (state.value.provider == AuthProvider.GOOGLE) {
                                googleAuthClient.signOut()
                            }
                            _uiEvent.send(UiEvent.Navigate(Onboarding))
                        }

                        is Result.Error -> showErrorSnackbar("Error occurred when trying to logout")

                        else -> Unit
                    }
                }
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun showErrorSnackbar(message: String) {
        coroutineScope.launch { snackbarController.sendEvent(SnackbarEvent(message = message)) }
    }

    private fun navigateDeleteAccount() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(DeleteAccount))
        }
    }

    private fun showSubscriptionDialog(show: Boolean) {
        _state.update {
            it.copy(
                showSubscriptionDialog = show
            )
        }
    }

    private fun openPrivacyPolicy() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(PrivacyPolicy))
        }
    }
}
