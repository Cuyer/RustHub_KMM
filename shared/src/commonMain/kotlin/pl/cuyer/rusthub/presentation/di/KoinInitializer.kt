package pl.cuyer.rusthub.presentation.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import pl.cuyer.rusthub.util.BuildType
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
import pl.cuyer.rusthub.data.local.search.SearchQueryDataSourceImpl
import pl.cuyer.rusthub.data.local.search.ItemSearchQueryDataSourceImpl
import pl.cuyer.rusthub.data.local.server.ServerDataSourceImpl
import pl.cuyer.rusthub.data.local.item.ItemDataSourceImpl
import pl.cuyer.rusthub.data.local.item.ItemSyncDataSourceImpl
import pl.cuyer.rusthub.data.local.monument.MonumentDataSourceImpl
import pl.cuyer.rusthub.data.local.monument.MonumentSyncDataSourceImpl
import pl.cuyer.rusthub.data.local.subscription.SubscriptionSyncDataSourceImpl
import pl.cuyer.rusthub.data.network.auth.AuthRepositoryImpl
import pl.cuyer.rusthub.data.network.config.ConfigRepositoryImpl
import pl.cuyer.rusthub.data.network.favourite.FavouriteClientImpl
import pl.cuyer.rusthub.data.network.filtersOptions.FiltersOptionsClientImpl
import pl.cuyer.rusthub.data.network.item.ItemsClientImpl
import pl.cuyer.rusthub.data.network.monument.MonumentsClientImpl
import pl.cuyer.rusthub.data.network.notification.MessagingTokenClientImpl
import pl.cuyer.rusthub.data.network.server.ServerClientImpl
import pl.cuyer.rusthub.data.network.subscription.SubscriptionClientImpl
import pl.cuyer.rusthub.data.network.user.UserRepositoryImpl
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository
import pl.cuyer.rusthub.domain.repository.config.ConfigRepository
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsRepository
import pl.cuyer.rusthub.domain.repository.item.ItemRepository
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource
import pl.cuyer.rusthub.domain.repository.monument.MonumentRepository
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentDataSource
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentSyncDataSource
import pl.cuyer.rusthub.domain.repository.notification.MessagingTokenRepository
import pl.cuyer.rusthub.domain.repository.search.SearchQueryDataSource
import pl.cuyer.rusthub.domain.repository.search.ItemSearchQueryDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerRepository
import pl.cuyer.rusthub.domain.repository.subscription.SubscriptionSyncDataSource
import pl.cuyer.rusthub.domain.repository.subscription.network.SubscriptionRepository
import pl.cuyer.rusthub.domain.repository.user.UserRepository
import pl.cuyer.rusthub.domain.usecase.AuthAnonymouslyUseCase
import pl.cuyer.rusthub.domain.usecase.ChangePasswordUseCase
import pl.cuyer.rusthub.domain.usecase.CheckEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.CheckUserExistsUseCase
import pl.cuyer.rusthub.domain.usecase.ClearFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteAccountUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteItemSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersOptionsUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.GetGoogleClientIdUseCase
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.GetPagedItemsUseCase
import pl.cuyer.rusthub.domain.usecase.GetSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetItemSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetServerDetailsUseCase
import pl.cuyer.rusthub.domain.usecase.GetItemDetailsUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserPreferencesUseCase
import pl.cuyer.rusthub.domain.usecase.LoginUserUseCase
import pl.cuyer.rusthub.domain.usecase.LoginWithGoogleUseCase
import pl.cuyer.rusthub.domain.usecase.LogoutUserUseCase
import pl.cuyer.rusthub.domain.usecase.RegisterUserUseCase
import pl.cuyer.rusthub.domain.usecase.RequestPasswordResetUseCase
import pl.cuyer.rusthub.domain.usecase.ResendConfirmationUseCase
import pl.cuyer.rusthub.domain.usecase.SetEmailConfirmedUseCase
import pl.cuyer.rusthub.domain.usecase.SetSubscribedUseCase
import pl.cuyer.rusthub.domain.usecase.SaveFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.SaveSearchQueryUseCase
import pl.cuyer.rusthub.domain.usecase.SaveItemSearchQueryUseCase
import pl.cuyer.rusthub.domain.usecase.ToggleFavouriteUseCase
import pl.cuyer.rusthub.domain.usecase.ToggleSubscriptionUseCase
import pl.cuyer.rusthub.domain.usecase.ConfirmPurchaseUseCase
import pl.cuyer.rusthub.domain.usecase.RefreshUserUseCase
import pl.cuyer.rusthub.domain.usecase.GetActiveSubscriptionUseCase
import pl.cuyer.rusthub.domain.usecase.UpgradeAccountUseCase
import pl.cuyer.rusthub.domain.usecase.UpgradeWithGoogleUseCase
import pl.cuyer.rusthub.domain.usecase.ClearServerCacheUseCase
import pl.cuyer.rusthub.domain.repository.server.ServerCacheDataSource
import pl.cuyer.rusthub.data.local.cache.ServerCacheDataSourceImpl
import pl.cuyer.rusthub.domain.usecase.SetThemeConfigUseCase
import pl.cuyer.rusthub.domain.usecase.SetDynamicColorPreferenceUseCase
import pl.cuyer.rusthub.domain.usecase.SetUseSystemColorsPreferenceUseCase
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.util.MessagingTokenManager
import pl.cuyer.rusthub.util.TokenRefresher
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.validator.EmailValidator
import pl.cuyer.rusthub.util.validator.PasswordValidator
import pl.cuyer.rusthub.util.validator.UsernameValidator
import pl.cuyer.rusthub.data.network.purchase.PurchaseRepositoryImpl
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository
import pl.cuyer.rusthub.data.local.purchase.PurchaseSyncDataSourceImpl
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource
import pl.cuyer.rusthub.domain.usecase.ads.GetNativeAdUseCase

val appModule = module {
    single<SnackbarController> { SnackbarController }
    single<UserEventController> { UserEventController }
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            explicitNulls = false
        }
    }
    singleOf(::TokenRefresher)
    singleOf(::ServerClientImpl) bind ServerRepository::class
    singleOf(::ItemsClientImpl) bind ItemRepository::class
    singleOf(::MonumentsClientImpl) bind MonumentRepository::class
    singleOf(::FavouriteClientImpl) bind FavouriteRepository::class
    singleOf(::SubscriptionClientImpl) bind SubscriptionRepository::class
    singleOf(::ServerDataSourceImpl) bind ServerDataSource::class
    singleOf(::ItemDataSourceImpl) bind ItemDataSource::class
    singleOf(::MonumentDataSourceImpl) bind MonumentDataSource::class
    singleOf(::ItemSyncDataSourceImpl) bind ItemSyncDataSource::class
    singleOf(::MonumentSyncDataSourceImpl) bind MonumentSyncDataSource::class
    singleOf(::FavouriteSyncDataSourceImpl) bind FavouriteSyncDataSource::class
    singleOf(::SubscriptionSyncDataSourceImpl) bind SubscriptionSyncDataSource::class
    singleOf(::PurchaseSyncDataSourceImpl) bind PurchaseSyncDataSource::class
    singleOf(::FiltersDataSourceImpl) bind FiltersDataSource::class
    singleOf(::SearchQueryDataSourceImpl) bind SearchQueryDataSource::class
    singleOf(::ItemSearchQueryDataSourceImpl) bind ItemSearchQueryDataSource::class
    singleOf(::MessagingTokenClientImpl) bind MessagingTokenRepository::class
    single { MessagingTokenManager(get(), get()) }
    singleOf(::FiltersOptionsClientImpl) bind FiltersOptionsRepository::class
    singleOf(::FiltersOptionsDataSourceImpl) bind FiltersOptionsDataSource::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::AuthDataSourceImpl) bind AuthDataSource::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::ConfigRepositoryImpl) bind ConfigRepository::class
    single { EmailValidator(get()) }
    single { PasswordValidator(get()) }
    single { UsernameValidator(get()) }
    single { GetPagedServersUseCase(get(), get(), get(), get()) }
    single { GetPagedItemsUseCase(get(), get()) }
    single { GetFiltersUseCase(get()) }
    single { SaveFiltersUseCase(get()) }
    single { SaveSearchQueryUseCase(get()) }
    single { SaveItemSearchQueryUseCase(get()) }
    single { ClearFiltersUseCase(get()) }
    singleOf(::ServerCacheDataSourceImpl) bind ServerCacheDataSource::class
    single { ClearServerCacheUseCase(get()) }
    single { GetFiltersOptionsUseCase(get(), get()) }
    single { GetSearchQueriesUseCase(get()) }
    single { GetItemSearchQueriesUseCase(get()) }
    single { DeleteSearchQueriesUseCase(get()) }
    single { DeleteItemSearchQueriesUseCase(get()) }
    single { GetServerDetailsUseCase(get()) }
    single { GetItemDetailsUseCase(get()) }
    single { RegisterUserUseCase(get(), get(), get(), get()) }
    single { LoginUserUseCase(get(), get(), get(), get()) }
    single { LoginWithGoogleUseCase(get(), get(), get(), get()) }
    single { GetGoogleClientIdUseCase(get()) }
    single { AuthAnonymouslyUseCase(get(), get(), get(), get()) }
    single { CheckUserExistsUseCase(get()) }
    single { CheckEmailConfirmedUseCase(get()) }
    single { ResendConfirmationUseCase(get()) }
    single { SetEmailConfirmedUseCase(get()) }
    single { SetSubscribedUseCase(get()) }
    single { GetUserUseCase(get()) }
    single { GetUserPreferencesUseCase(get()) }
    single { SetThemeConfigUseCase(get()) }
    single { SetDynamicColorPreferenceUseCase(get()) }
    single { SetUseSystemColorsPreferenceUseCase(get()) }
    single { LogoutUserUseCase(get(), get()) }
    single { DeleteAccountUseCase(get(), get()) }
    single { ChangePasswordUseCase(get()) }
    single { RequestPasswordResetUseCase(get()) }
    single { UpgradeAccountUseCase(get(), get(), get(), get()) }
    single { UpgradeWithGoogleUseCase(get(), get(), get(), get()) }
    single { ToggleFavouriteUseCase(get(), get(), get(), get()) }
    single { ToggleSubscriptionUseCase(get(), get(), get(), get()) }
    singleOf(::PurchaseRepositoryImpl) bind PurchaseRepository::class
    single { ConfirmPurchaseUseCase(get(), get(), get()) }
    single { GetActiveSubscriptionUseCase(get(), get()) }
    single { RefreshUserUseCase(get(), get(), get()) }
    single { GetNativeAdUseCase(get()) }
}

expect fun platformModule(passphrase: String): Module
expect val userPreferencesModule: Module

fun initKoin(passphrase: String = "", appDeclaration: KoinAppDeclaration = {}) = startKoin {
    if (BuildType.isDebug) {
        Napier.base(DebugAntilog())
    }
    appDeclaration()
    modules(appModule, userPreferencesModule, platformModule(passphrase))
}
