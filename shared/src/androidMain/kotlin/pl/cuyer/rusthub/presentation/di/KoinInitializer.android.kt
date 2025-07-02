package pl.cuyer.rusthub.presentation.di

import dev.icerock.moko.permissions.PermissionsController
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory
import pl.cuyer.rusthub.data.network.HttpClientFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsViewModel
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountViewModel
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerViewModel
import pl.cuyer.rusthub.presentation.features.settings.SettingsViewModel
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.util.ClipboardHandler
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.util.LogoutScheduler
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.util.MessagingTokenScheduler
import pl.cuyer.rusthub.util.StoreNavigator
import pl.cuyer.rusthub.util.SubscriptionSyncScheduler
import pl.cuyer.rusthub.util.SyncScheduler

actual val platformModule: Module = module {
    single<RustHubDatabase> { DatabaseDriverFactory(androidContext()).create() }
    single { HttpClientFactory(get(), get()).create() }
    single { ClipboardHandler(get()) }
    single { SyncScheduler(get()) }
    single { SubscriptionSyncScheduler(get()) }
    single { MessagingTokenScheduler(get()) }
    single { LogoutScheduler(androidContext()) }
    single { StoreNavigator(androidContext()) }
    single { GoogleAuthClient(androidContext()) }
    single { PermissionsController(androidContext()) }
    viewModel {
        StartupViewModel(get(), get())
    }
    viewModel {
        OnboardingViewModel(
            authAnonymouslyUseCase = get(),
            checkUserExistsUseCase = get(),
            loginWithGoogleUseCase = get(),
            getGoogleClientIdUseCase = get(),
            googleAuthClient = get(),
            snackbarController = get(),
            emailValidator = get()
        )
    }
    viewModel { (email: String, exists: Boolean, provider: AuthProvider?) ->
        CredentialsViewModel(
            email = email,
            userExists = exists,
            provider = provider,
            loginUserUseCase = get(),
            registerUserUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            usernameValidator = get(),
            loginWithGoogleUseCase = get(),
            getGoogleClientIdUseCase = get(),
            googleAuthClient = get()
        )
    }
    viewModel {
        ServerViewModel(
            clipboardHandler = get(),
            snackbarController = get(),
            getPagedServersUseCase = get(),
            getFiltersUseCase = get(),
            getFiltersOptions = get(),
            saveFiltersUseCase = get(),
            clearFiltersUseCase = get(),
            saveSearchQueryUseCase = get(),
            getSearchQueriesUseCase = get(),
            deleteSearchQueriesUseCase = get()
        )
    }
    viewModel {
        SettingsViewModel(
            getSettingsUseCase = get(),
            saveSettingsUseCase = get(),
            logoutUserUseCase = get(),
            getUserUseCase = get(),
            permissionsController = get(),
            googleAuthClient = get()
        )
    }
    viewModel {
        DeleteAccountViewModel(
            deleteAccountUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            usernameValidator = get(),
            getUserUseCase = get()
        )
    }
    viewModel {
        UpgradeViewModel(
            upgradeAccountUseCase = get(),
            snackbarController = get(),
            usernameValidator = get(),
            passwordValidator = get()
        )
    }
    viewModel { (serverId: Long, serverName: String?) ->
        ServerDetailsViewModel(
            getServerDetailsUseCase = get(),
            toggleFavouriteUseCase = get(),
            toggleSubscriptionUseCase = get(),
            permissionsController = get(),
            serverName = serverName,
            serverId = serverId,
            clipboardHandler = get(),
            snackbarController = get()
        )
    }
}