package pl.cuyer.rusthub.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory
import pl.cuyer.rusthub.data.network.HttpClientFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.presentation.features.auth.login.LoginViewModel
import pl.cuyer.rusthub.presentation.features.auth.register.RegisterViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.util.ClipboardHandler
import pl.cuyer.rusthub.util.SyncScheduler
import pl.cuyer.rusthub.util.SubscriptionSyncScheduler
import pl.cuyer.rusthub.util.TopicSubscriber

actual val platformModule: Module = module {
    single<RustHubDatabase> { DatabaseDriverFactory().create() }
    single { HttpClientFactory(get(), get()).create() }
    single { ClipboardHandler() }
    single { SyncScheduler() }
    single { SubscriptionSyncScheduler() }
    single { TopicSubscriber() }
    factory { StartupViewModel(get()) }
    factory {
        OnboardingViewModel(
            authAnonymouslyUseCase = get(),
            snackbarController = get(),
        )
    }
    factory {
        LoginViewModel(
            loginUserUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            usernameValidator = get()
        )
    }
    factory {
        RegisterViewModel(
            registerUserUseCase = get(),
            snackbarController = get(),
            emailValidator = get(),
            passwordValidator = get(),
            usernameValidator = get()
        )
    }
}