package pl.cuyer.rusthub.presentation.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.auth.AuthDataSourceImpl
import pl.cuyer.rusthub.data.local.favourite.FavouriteSyncDataSourceImpl
import pl.cuyer.rusthub.data.local.filter.FiltersDataSourceImpl
import pl.cuyer.rusthub.data.local.filtersOptions.FiltersOptionsDataSourceImpl
import pl.cuyer.rusthub.data.local.remotekey.RemoteKeyDataSourceImpl
import pl.cuyer.rusthub.data.local.search.SearchQueryDataSourceImpl
import pl.cuyer.rusthub.data.local.server.ServerDataSourceImpl
import pl.cuyer.rusthub.data.local.settings.SettingsDataSourceImpl
import pl.cuyer.rusthub.data.local.subscription.SubscriptionSyncDataSourceImpl
import pl.cuyer.rusthub.data.network.auth.AuthRepositoryImpl
import pl.cuyer.rusthub.data.network.favourite.FavouriteClientImpl
import pl.cuyer.rusthub.data.network.filtersOptions.FiltersOptionsClientImpl
import pl.cuyer.rusthub.data.network.notification.MessagingTokenClientImpl
import pl.cuyer.rusthub.data.network.server.ServerClientImpl
import pl.cuyer.rusthub.data.network.subscription.SubscriptionClientImpl
import pl.cuyer.rusthub.domain.repository.RemoteKeyDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsRepository
import pl.cuyer.rusthub.domain.repository.notification.MessagingTokenRepository
import pl.cuyer.rusthub.domain.repository.search.SearchQueryDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerRepository
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource
import pl.cuyer.rusthub.domain.repository.subscription.SubscriptionSyncDataSource
import pl.cuyer.rusthub.domain.repository.subscription.network.SubscriptionRepository
import pl.cuyer.rusthub.domain.usecase.AuthAnonymouslyUseCase
import pl.cuyer.rusthub.domain.usecase.ClearFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersOptionsUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.GetSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetServerDetailsUseCase
import pl.cuyer.rusthub.domain.usecase.GetSettingsUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.LoginUserUseCase
import pl.cuyer.rusthub.domain.usecase.LoginWithGoogleUseCase
import pl.cuyer.rusthub.domain.usecase.LogoutUserUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteAccountUseCase
import pl.cuyer.rusthub.domain.usecase.UpgradeAccountUseCase
import pl.cuyer.rusthub.domain.usecase.CheckUserExistsUseCase
import pl.cuyer.rusthub.domain.usecase.RegisterUserUseCase
import pl.cuyer.rusthub.domain.usecase.SaveFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.SaveSearchQueryUseCase
import pl.cuyer.rusthub.domain.usecase.SaveSettingsUseCase
import pl.cuyer.rusthub.domain.usecase.ToggleFavouriteUseCase
import pl.cuyer.rusthub.domain.usecase.ToggleSubscriptionUseCase
import pl.cuyer.rusthub.presentation.settings.SettingsController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.util.MessagingTokenManager
import pl.cuyer.rusthub.domain.repository.config.ConfigRepository
import pl.cuyer.rusthub.data.network.config.ConfigRepositoryImpl
import pl.cuyer.rusthub.domain.usecase.GetGoogleClientIdUseCase
import pl.cuyer.rusthub.util.validator.EmailValidator
import pl.cuyer.rusthub.util.validator.PasswordValidator
import pl.cuyer.rusthub.util.validator.UsernameValidator

val appModule = module {
    single<SnackbarController> { SnackbarController }
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            explicitNulls = false
        }
    }
    singleOf(::ServerClientImpl) bind ServerRepository::class
    singleOf(::FavouriteClientImpl) bind FavouriteRepository::class
    singleOf(::SubscriptionClientImpl) bind SubscriptionRepository::class
    singleOf(::ServerDataSourceImpl) bind ServerDataSource::class
    singleOf(::FavouriteSyncDataSourceImpl) bind FavouriteSyncDataSource::class
    singleOf(::SubscriptionSyncDataSourceImpl) bind SubscriptionSyncDataSource::class
    singleOf(::FiltersDataSourceImpl) bind FiltersDataSource::class
    singleOf(::SearchQueryDataSourceImpl) bind SearchQueryDataSource::class
    singleOf(::RemoteKeyDataSourceImpl) bind RemoteKeyDataSource::class
    singleOf(::SettingsDataSourceImpl) bind SettingsDataSource::class
    singleOf(::MessagingTokenClientImpl) bind MessagingTokenRepository::class
    single { MessagingTokenManager(get(), get()) }
    single { GetPagedServersUseCase(get(), get(), get(), get()) }
    singleOf(::FiltersOptionsClientImpl) bind FiltersOptionsRepository::class
    singleOf(::FiltersOptionsDataSourceImpl) bind FiltersOptionsDataSource::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::AuthDataSourceImpl) bind AuthDataSource::class
    singleOf(::ConfigRepositoryImpl) bind ConfigRepository::class
    single { EmailValidator }
    single { PasswordValidator }
    single { UsernameValidator }
    single { GetPagedServersUseCase(get(), get(), get(), get()) }
    single { GetFiltersUseCase(get()) }
    single { SaveFiltersUseCase(get()) }
    single { SaveSearchQueryUseCase(get()) }
    single { ClearFiltersUseCase(get()) }
    single { GetFiltersOptionsUseCase(get(), get()) }
    single { GetSearchQueriesUseCase(get()) }
    single { DeleteSearchQueriesUseCase(get()) }
    single { GetServerDetailsUseCase(get()) }
    single { RegisterUserUseCase(get(), get(), get()) }
    single { LoginUserUseCase(get(), get(), get()) }
    single { LoginWithGoogleUseCase(get(), get(), get()) }
    single { GetGoogleClientIdUseCase(get()) }
    single { AuthAnonymouslyUseCase(get(), get(), get()) }
    single { CheckUserExistsUseCase(get()) }
    single { GetUserUseCase(get()) }
    single { LogoutUserUseCase(get(), get()) }
    single { DeleteAccountUseCase(get(), get()) }
    single { UpgradeAccountUseCase(get(), get(), get()) }
    single { GetSettingsUseCase(get()) }
    single { SaveSettingsUseCase(get()) }
    single { SettingsController(get()) }
    single { ToggleFavouriteUseCase(get(), get(), get(), get()) }
    single { ToggleSubscriptionUseCase(get(), get(), get(), get()) }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    Napier.base(DebugAntilog())
    appDeclaration()
    modules(appModule, platformModule)
    printLogger()
}
