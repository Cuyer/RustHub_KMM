package pl.cuyer.rusthub.presentation.features.settings

import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import pl.cuyer.rusthub.util.catchAndLog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.usecase.GetUserPreferencesUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.LogoutUserUseCase
import pl.cuyer.rusthub.domain.usecase.SetDynamicColorPreferenceUseCase
import pl.cuyer.rusthub.domain.usecase.SetThemeConfigUseCase
import pl.cuyer.rusthub.domain.usecase.SetUseSystemColorsPreferenceUseCase
import pl.cuyer.rusthub.presentation.navigation.ChangePassword
import pl.cuyer.rusthub.presentation.navigation.DeleteAccount
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.PrivacyPolicy
import pl.cuyer.rusthub.presentation.navigation.Subscription
import pl.cuyer.rusthub.presentation.navigation.ConfirmEmail
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.navigation.UpgradeAccount
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.common.user.UserEvent
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.toUserMessage
import pl.cuyer.rusthub.util.SystemDarkThemeObserver
import pl.cuyer.rusthub.util.anonymousAccountExpiresIn
import pl.cuyer.rusthub.util.formatExpiration
import pl.cuyer.rusthub.util.ItemsScheduler
import pl.cuyer.rusthub.domain.model.ActiveSubscription
import pl.cuyer.rusthub.domain.usecase.GetActiveSubscriptionUseCase
import pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource
import pl.cuyer.rusthub.domain.model.ItemSyncState
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.util.updateAppLanguage

class SettingsViewModel(
    private val logoutUserUseCase: LogoutUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val setThemeConfigUseCase: SetThemeConfigUseCase,
    private val setDynamicColorPreferenceUseCase: SetDynamicColorPreferenceUseCase,
    private val setUseSystemColorsPreferenceUseCase: SetUseSystemColorsPreferenceUseCase,
    private val permissionsController: PermissionsController,
    private val googleAuthClient: GoogleAuthClient,
    private val snackbarController: SnackbarController,
    private val stringProvider: StringProvider,
    private val systemDarkThemeObserver: SystemDarkThemeObserver,
    private val itemsScheduler: ItemsScheduler,
    private val getActiveSubscriptionUseCase: GetActiveSubscriptionUseCase,
    private val itemSyncDataSource: ItemSyncDataSource,
    private val userEventController: UserEventController
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private var logoutJob: Job? = null
    private var emailConfirmed: Boolean = true
    private var subscriptionJob: Job? = null
    private val _state = MutableStateFlow(SettingsState())
    val state = _state
        .onStart {
            observeUser()
            observePreferences()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsState()
        )

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnChangePasswordClick -> navigateChangePassword()
            SettingsAction.OnNotificationsClick -> permissionsController.openAppSettings()
            SettingsAction.OnLogout -> logout()
            SettingsAction.OnSubscriptionClick -> {
                if (!emailConfirmed) {
                    showUnconfirmedSnackbar()
                } else {
                    navigateSubscription()
                }
            }
            SettingsAction.OnDismissSubscriptionDialog -> Unit
            SettingsAction.OnSubscribe -> Unit
            SettingsAction.OnPrivacyPolicy -> openPrivacyPolicy()
            SettingsAction.OnDeleteAccount -> navigateDeleteAccount()
            SettingsAction.OnUpgradeAccount -> navigateUpgrade()
            SettingsAction.OnResume -> refreshSubscription()
            is SettingsAction.OnThemeChange -> setTheme(action.theme)
            is SettingsAction.OnDynamicColorsChange -> setDynamicColors(action.enabled)
            is SettingsAction.OnUseSystemColorsChange -> setUseSystemColors(action.enabled)
            is SettingsAction.OnLanguageChange -> changeLanguage(action.language)
        }
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

    private fun observePreferences() {
        getUserPreferencesUseCase()
            .combine(systemDarkThemeObserver.isSystemDarkTheme) { prefs, systemDark ->
                val theme = if (prefs.useSystemColors) {
                    if (systemDark) Theme.DARK else Theme.LIGHT
                } else {
                    when (prefs.themeConfig) {
                        Theme.SYSTEM -> if (systemDark) Theme.DARK else Theme.LIGHT
                        else -> prefs.themeConfig
                    }
                }
                Triple(theme, prefs.useDynamicColor, prefs.useSystemColors)
            }
            .onEach { (theme, dynamic, systemColors) ->
                _state.update { it.copy(theme = theme, dynamicColors = dynamic, useSystemColors = systemColors) }
            }
            .launchIn(coroutineScope)
    }

    private fun observeUser() {
        getUserUseCase()
            .onEach { user ->
                _state.update { it.copy(currentUser = user) }
                updateUser(user, state.value.currentSubscription)
            }
            .launchIn(coroutineScope)
    }

    private fun updateUser(user: User?, subscription: ActiveSubscription?) {
        emailConfirmed = user?.emailConfirmed == true
        _state.update {
            it.copy(
                username = if (user?.provider == AuthProvider.GOOGLE) user.username.substringBefore("-") else user?.username,
                provider = user?.provider,
                subscribed = subscription != null,
                currentPlan = subscription?.plan,
                subscriptionExpiration = subscription?.expiration?.toString(),
                subscriptionStatus = subscription?.state?.displayName(stringProvider),
                anonymousExpiration = user?.let { u ->
                    if (u.provider == AuthProvider.ANONYMOUS) {
                        anonymousAccountExpiresIn(u.accessToken)?.let { formatExpiration(it, stringProvider) }
                    } else {
                        null
                    }
                },
                currentSubscription = subscription,
                currentUser = user
            )
        }
    }

    private fun logout() {
        logoutJob?.cancel()
        logoutJob = coroutineScope.launch {
            logoutUserUseCase()
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catchAndLog { e ->
                    showErrorSnackbar(e.toUserMessage(stringProvider))
                }
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            if (state.value.provider == AuthProvider.GOOGLE) {
                                googleAuthClient.signOut()
                            }
                            userEventController.sendEvent(UserEvent.LoggedOut)
                        }

                        is Result.Error -> showErrorSnackbar(stringProvider.get(SharedRes.strings.logout_error))

                    }
                }
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun setTheme(theme: Theme) {
        coroutineScope.launch { setThemeConfigUseCase(theme) }
    }

    private fun setDynamicColors(enabled: Boolean) {
        coroutineScope.launch { setDynamicColorPreferenceUseCase(enabled) }
    }

    private fun setUseSystemColors(enabled: Boolean) {
        coroutineScope.launch { setUseSystemColorsPreferenceUseCase(enabled) }
    }

    private fun refreshSubscription() {
        subscriptionJob?.cancel()
        subscriptionJob = coroutineScope.launch {
            getActiveSubscriptionUseCase()
                .onStart { updateLoading(true) }
                .onCompletion { updateLoading(false) }
                .catchAndLog { updateUser(state.value.currentUser, null) }
                .collectLatest { sub ->
                    val user = getUserUseCase().first()
                    updateUser(user, sub)
                }
        }
    }

    private fun showErrorSnackbar(message: String?) {
        message ?: return
        coroutineScope.launch { snackbarController.sendEvent(SnackbarEvent(message = message)) }
    }

    private fun navigateDeleteAccount() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(DeleteAccount))
        }
    }

    private fun navigateSubscription() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(Subscription))
        }
    }

    private fun navigateConfirmEmail() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(ConfirmEmail))
        }
    }

    private fun showUnconfirmedSnackbar() {
        coroutineScope.launch {
            snackbarController.sendEvent(
                SnackbarEvent(
                    message = stringProvider.get(SharedRes.strings.email_not_confirmed),
                    action = SnackbarAction(stringProvider.get(SharedRes.strings.resend)) {
                        navigateConfirmEmail()
                    }
                )
            )
        }
    }

    private fun changeLanguage(language: Language) {
        coroutineScope.launch {
            updateAppLanguage(language)
            itemSyncDataSource.setState(ItemSyncState.PENDING)
            itemsScheduler.startNow()
            itemsScheduler.schedule()
        }
    }

    private fun openPrivacyPolicy() {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(PrivacyPolicy))
        }
    }
}
