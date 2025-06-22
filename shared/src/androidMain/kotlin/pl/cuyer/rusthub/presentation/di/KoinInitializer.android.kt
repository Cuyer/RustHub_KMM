package pl.cuyer.rusthub.presentation.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory
import pl.cuyer.rusthub.data.network.HttpClientFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.presentation.features.auth.LoginViewModel
import pl.cuyer.rusthub.presentation.features.auth.register.RegisterViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerViewModel
import pl.cuyer.rusthub.util.ClipboardHandler

actual val platformModule: Module = module {
    single<RustHubDatabase> { DatabaseDriverFactory(androidContext()).create() }
    single { HttpClientFactory(get(), get()).create() }
    single { ClipboardHandler(get()) }
    viewModel {
        StartupViewModel(get())
    }
    viewModel {
        OnboardingViewModel()
    }
    viewModel {
        LoginViewModel(
            loginUserUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            usernameValidator = get()
        )
    }
    viewModel {
        RegisterViewModel(
            registerUserUseCase = get(),
            snackbarController = get(),
            emailValidator = get(),
            passwordValidator = get(),
            usernameValidator = get()
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
    viewModel { (serverId: Long, serverName: String?) ->
        ServerDetailsViewModel(
            getServerDetailsUseCase = get(),
            serverName = serverName,
            serverId = serverId,
            clipboardHandler = get(),
            snackbarController = get()
        )
    }
}