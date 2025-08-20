package pl.cuyer.rusthub.presentation.features.startup

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import pl.cuyer.rusthub.util.catchAndLog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.CancellationException
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.model.UserPreferences
import pl.cuyer.rusthub.domain.usecase.GetUserPreferencesUseCase
import pl.cuyer.rusthub.domain.usecase.CheckEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.SetEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.SetSubscribedUseCase
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.getCurrentAppLanguage
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource
import pl.cuyer.rusthub.util.InAppUpdateManager
import pl.cuyer.rusthub.util.PurchaseSyncScheduler
import pl.cuyer.rusthub.util.MonumentsScheduler
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentDataSource
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentSyncDataSource
import pl.cuyer.rusthub.domain.model.MonumentSyncState

class StartupViewModel(
    private val snackbarController: SnackbarController,
    private val getUserUseCase: GetUserUseCase,
    private val checkEmailConfirmedUseCase: CheckEmailConfirmedUseCase,
    private val setEmailConfirmedUseCase: SetEmailConfirmedUseCase,
    private val setSubscribedUseCase: SetSubscribedUseCase,
    private val stringProvider: StringProvider,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val monumentsScheduler: MonumentsScheduler,
    private val monumentDataSource: MonumentDataSource,
    private val monumentSyncDataSource: MonumentSyncDataSource,
    private val purchaseSyncDataSource: PurchaseSyncDataSource,
    private val purchaseSyncScheduler: PurchaseSyncScheduler
) : BaseViewModel() {

    private val initializationJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
        initialize()
    }
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
        observePreferences()
        coroutineScope.launch {
            if (monumentDataSource.isEmpty(getCurrentAppLanguage())) {
                monumentSyncDataSource.setState(MonumentSyncState.PENDING)
                monumentsScheduler.startNow()
            } else {
                monumentsScheduler.schedule()
            }
            if (purchaseSyncDataSource.getPendingOperations().isNotEmpty()) {
                purchaseSyncScheduler.schedule()
            }
            initializationJob.start()
        }
    }

    private fun observePreferences() {
        preferencesFlow
            .onEach { prefs ->
                val theme = if (prefs.useSystemColors) Theme.SYSTEM else prefs.themeConfig
                updateTheme(theme, prefs.useDynamicColor)
            }
            .catchAndLog { e ->
                Napier.e("Error reading preferences", e)
            }
            .launchIn(coroutineScope)
    }

    private suspend fun initialize() {
        updateLoadingState(true)
        try {
            val user = getUserUseCase().first()
            if (user != null && user.provider == AuthProvider.LOCAL) {
                when (val result = checkEmailConfirmedUseCase().first()) {
                    is Result.Success -> {
                        setEmailConfirmedUseCase(result.data.emailConfirmed)
                        setSubscribedUseCase(result.data.subscribed)
                    }
                    is Result.Error -> Unit
                }
            }
            updateStartDestination(user)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
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

    private fun showErrorSnackbar(message: String?) {
        message ?: return
        coroutineScope.launch {
            snackbarController.sendEvent(SnackbarEvent(message = message))
        }
    }
}