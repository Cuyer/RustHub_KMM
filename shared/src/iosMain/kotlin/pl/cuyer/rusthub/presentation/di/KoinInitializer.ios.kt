package pl.cuyer.rusthub.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory
import pl.cuyer.rusthub.data.network.HttpClientFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsViewModel
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.usecase.LoginWithGoogleUseCase
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.features.settings.SettingsViewModel
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountViewModel
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeViewModel
import pl.cuyer.rusthub.util.ClipboardHandler
import pl.cuyer.rusthub.util.SyncScheduler
import pl.cuyer.rusthub.util.SubscriptionSyncScheduler
import pl.cuyer.rusthub.util.StoreNavigator
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.util.MessagingTokenScheduler
import pl.cuyer.rusthub.util.LogoutScheduler
import dev.icerock.moko.permissions.PermissionsController

actual val platformModule: Module = module {
    single<RustHubDatabase> { DatabaseDriverFactory().create() }
    single { HttpClientFactory(get(), get()).create() }
    single { ClipboardHandler() }
    single { SyncScheduler() }
    single { SubscriptionSyncScheduler() }
    single { MessagingTokenScheduler() }
    single { LogoutScheduler() }
    single { StoreNavigator() }
    single { GoogleAuthClient() }
    single { PermissionsController() }
    factory { StartupViewModel(get()) }
    factory {
        OnboardingViewModel(
            authAnonymouslyUseCase = get(),
            checkUserExistsUseCase = get(),
            loginWithGoogleUseCase = get(),
            getGoogleClientIdUseCase = get(),
            googleAuthClient = get(),
            snackbarController = get(),
            loginWithGoogleUseCase = get(),
            getGoogleClientIdUseCase = get(),
            googleAuthClient = get()
        )
    }
    factory { (email: String, exists: Boolean, provider: AuthProvider?) ->
        CredentialsViewModel(
            email = email,
            userExists = exists,
            provider = provider,
            loginUserUseCase = get(),
            registerUserUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            usernameValidator = get()
        )
    }
    factory {
        SettingsViewModel(
            getSettingsUseCase = get(),
            saveSettingsUseCase = get(),
            logoutUserUseCase = get(),
            getUserUseCase = get(),
            permissionsController = get(),
            googleAuthClient = get()
        )
    }
    factory {
        DeleteAccountViewModel(
            deleteAccountUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            usernameValidator = get(),
            getUserUseCase = get()
        )
    }
    factory {
        UpgradeViewModel(
            upgradeAccountUseCase = get(),
            snackbarController = get(),
            usernameValidator = get(),
            passwordValidator = get()
        )
    }
}