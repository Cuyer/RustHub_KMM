package pl.cuyer.rusthub.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory
import pl.cuyer.rusthub.data.network.HttpClientFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsViewModel
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ChangePasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.settings.SettingsViewModel
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailViewModel
import pl.cuyer.rusthub.util.ClipboardHandler
import pl.cuyer.rusthub.domain.usecase.ResendConfirmationUseCase
import pl.cuyer.rusthub.domain.usecase.SetEmailConfirmedUseCase
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.util.MessagingTokenScheduler
import pl.cuyer.rusthub.util.StoreNavigator
import pl.cuyer.rusthub.util.ReviewRequester
import pl.cuyer.rusthub.util.SubscriptionSyncScheduler
import pl.cuyer.rusthub.util.SyncScheduler
import pl.cuyer.rusthub.util.TokenRefresher
import pl.cuyer.rusthub.util.ShareHandler
import pl.cuyer.rusthub.util.InAppUpdateManager
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.AppCheckTokenProvider
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource
import pl.cuyer.rusthub.data.local.settings.createSettingsDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual val platformModule: Module = module {
    single<RustHubDatabase> { DatabaseDriverFactory().create() }
    single { AppCheckTokenProvider() }
    single { HttpClientFactory(get(), get(), get()).create() }
    single { TokenRefresher(get()) }
    single { ClipboardHandler() }
    single { ShareHandler() }
    single { SyncScheduler() }
    single { SubscriptionSyncScheduler() }
    single { MessagingTokenScheduler() }
    single { InAppUpdateManager() }
    single { ReviewRequester() }
    single { StoreNavigator() }
    single { GoogleAuthClient() }
    single { StringProvider() }
    single<DataStore<Preferences>> { createSettingsDataStore() }
    single { SettingsDataSourceImpl(get()) } bind SettingsDataSource::class
    single { PermissionsController() }
    factory { StartupViewModel(get(), get(), get(), get(), get()) }
    factory {
        ConfirmEmailViewModel(
            checkEmailConfirmedUseCase = get(),
            getUserUseCase = get(),
            resendConfirmationUseCase = get(),
            snackbarController = get(),
            setEmailConfirmedUseCase = get(),
            stringProvider = get(),
        )
    }
    factory {
        OnboardingViewModel(
            authAnonymouslyUseCase = get(),
            checkUserExistsUseCase = get(),
            loginWithGoogleUseCase = get(),
            getGoogleClientIdUseCase = get(),
            googleAuthClient = get(),
            snackbarController = get(),
            emailValidator = get(),
            stringProvider = get()
        )
    }
    factory { (email: String, exists: Boolean, provider: AuthProvider?) ->
        CredentialsViewModel(
            email = email,
            userExists = exists,
            provider = provider,
            loginUserUseCase = get(),
            registerUserUseCase = get(),
            checkEmailConfirmedUseCase = get(),
            getUserUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            usernameValidator = get(),
            stringProvider = get()
        )
    }
    factory {
        SettingsViewModel(
            getSettingsUseCase = get(),
            saveSettingsUseCase = get(),
            logoutUserUseCase = get(),
            getUserUseCase = get(),
            permissionsController = get(),
            googleAuthClient = get(),
            snackbarController = get(),
            stringProvider = get()
        )
    }
    factory {
        DeleteAccountViewModel(
            deleteAccountUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            getUserUseCase = get(),
            stringProvider = get()
        )
    }
    factory {
        ChangePasswordViewModel(
            changePasswordUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            stringProvider = get(),
        )
    }
    factory { (email: String) ->
        ResetPasswordViewModel(
            email = email,
            requestPasswordResetUseCase = get(),
            snackbarController = get(),
            emailValidator = get(),
            stringProvider = get()
        )
    }
    factory {
        UpgradeViewModel(
            upgradeAccountUseCase = get(),
            upgradeWithGoogleUseCase = get(),
            getGoogleClientIdUseCase = get(),
            googleAuthClient = get(),
            snackbarController = get(),
            usernameValidator = get(),
            passwordValidator = get(),
            emailValidator = get(),
            stringProvider = get()
        )
    }
}
