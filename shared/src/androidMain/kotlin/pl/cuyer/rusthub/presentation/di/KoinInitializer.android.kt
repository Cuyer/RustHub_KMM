package pl.cuyer.rusthub.presentation.di

import dev.icerock.moko.permissions.PermissionsController
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.cuyer.rusthub.BuildConfig
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory
import pl.cuyer.rusthub.data.local.item.ItemSyncDataSourceImpl
import pl.cuyer.rusthub.data.network.HttpClientFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailViewModel
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsViewModel
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ChangePasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeViewModel
import pl.cuyer.rusthub.presentation.features.item.ItemViewModel
import pl.cuyer.rusthub.presentation.features.item.ItemDetailsViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerViewModel
import pl.cuyer.rusthub.presentation.features.settings.SettingsViewModel
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.domain.usecase.ClearServerCacheUseCase
import pl.cuyer.rusthub.util.AppCheckTokenProvider
import pl.cuyer.rusthub.util.ClipboardHandler
import pl.cuyer.rusthub.util.ConnectivityObserver
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.util.InAppUpdateManager
import pl.cuyer.rusthub.util.ItemsScheduler
import pl.cuyer.rusthub.util.MessagingTokenScheduler
import pl.cuyer.rusthub.util.ReviewRequester
import pl.cuyer.rusthub.util.ShareHandler
import pl.cuyer.rusthub.util.StoreNavigator
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.SubscriptionSyncScheduler
import pl.cuyer.rusthub.util.SyncScheduler
import pl.cuyer.rusthub.util.AdsConsentManager
import pl.cuyer.rusthub.data.ads.NativeAdRepositoryImpl
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository
import pl.cuyer.rusthub.domain.usecase.ads.GetNativeAdUseCase
import pl.cuyer.rusthub.domain.usecase.ads.ClearNativeAdsUseCase
import pl.cuyer.rusthub.presentation.features.ads.NativeAdViewModel
import pl.cuyer.rusthub.util.ActivityProvider
import pl.cuyer.rusthub.util.PurchaseSyncScheduler
import pl.cuyer.rusthub.util.UserSyncScheduler
import pl.cuyer.rusthub.domain.usecase.SetSubscribedUseCase
import pl.cuyer.rusthub.data.local.purchase.PurchaseSyncDataSourceImpl
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource
import pl.cuyer.rusthub.util.SystemDarkThemeObserver
import pl.cuyer.rusthub.data.billing.BillingRepositoryImpl
import pl.cuyer.rusthub.domain.repository.purchase.BillingRepository
import pl.cuyer.rusthub.presentation.features.subscription.SubscriptionViewModel
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan
import org.koin.android.ext.koin.androidApplication
import pl.cuyer.rusthub.util.EmailSender
import pl.cuyer.rusthub.util.UrlOpener
import pl.cuyer.rusthub.util.RemoteConfig

actual fun platformModule(passphrase: String): Module = module {
    single<RustHubDatabase>(createdAtStart = true) {
        if (BuildConfig.USE_ENCRYPTED_DB) {
            DatabaseDriverFactory(androidContext(), passphrase).create()
        } else {
            DatabaseDriverFactory(androidContext()).create()
        }
    }
    single { AppCheckTokenProvider() }
    single { HttpClientFactory(get(), get(), get(), get(), get()).create() }
    single { ClipboardHandler(get()) }
    single { ShareHandler(get<ActivityProvider>()) }
    single { AdsConsentManager.getInstance(get()) }
    single(createdAtStart = true) { ActivityProvider(androidApplication()) }
    single<NativeAdRepository> { NativeAdRepositoryImpl(get()) }
    factory { GetNativeAdUseCase(get()) }
    factory { ClearNativeAdsUseCase(get()) }
    viewModel { NativeAdViewModel(get(), get()) }
    single { SyncScheduler(get()) }
    single { SubscriptionSyncScheduler(get()) }
    single { MessagingTokenScheduler(get()) }
    single { ItemsScheduler(get()) }
    single { PurchaseSyncScheduler(get()) }
    single { UserSyncScheduler(get()) }
    single { BillingRepositoryImpl(androidContext()) } bind BillingRepository::class
    single { ItemSyncDataSourceImpl(get()) } bind ItemSyncDataSource::class
    single { PurchaseSyncDataSourceImpl(get()) } bind PurchaseSyncDataSource::class
    single { InAppUpdateManager(get(), get(), get()) }
    single { ReviewRequester(get()) }
    single { StoreNavigator(get()) }
    single { UrlOpener(get()) }
    single { EmailSender(get()) }
    single { SystemDarkThemeObserver(androidContext()) }
    single { ConnectivityObserver(androidContext()) }
    single { GoogleAuthClient(get()) }
    single { RemoteConfig() }
    single { StringProvider(androidContext()) }
    single { PermissionsController(androidContext()) }
    viewModel {
        StartupViewModel(
            snackbarController = get(),
            getUserUseCase = get(),
            checkEmailConfirmedUseCase = get(),
            setEmailConfirmedUseCase = get(),
            setSubscribedUseCase = get(),
            stringProvider = get(),
            getUserPreferencesUseCase = get(),
            itemsScheduler = get(),
            itemDataSource = get(),
            itemSyncDataSource = get(),
            purchaseSyncDataSource = get(),
            purchaseSyncScheduler = get()
        )
    }
    viewModel {
        OnboardingViewModel(
            authAnonymouslyUseCase = get(),
            checkUserExistsUseCase = get(),
            loginWithGoogleUseCase = get(),
            getGoogleClientIdUseCase = get(),
            googleAuthClient = get(),
            snackbarController = get(),
            emailValidator = get(),
            stringProvider = get(),
            remoteConfig = get()
        )
    }
    viewModel { (email: String, exists: Boolean, provider: AuthProvider?) ->
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
            loginWithGoogleUseCase = get(),
            getGoogleClientIdUseCase = get(),
            googleAuthClient = get(),
            remoteConfig = get(),
            stringProvider = get()
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
            deleteSearchQueriesUseCase = get(),
            clearServerCacheUseCase = get(),
            stringProvider = get(),
            connectivityObserver = get(),
            getUserUseCase = get(),
            adsConsentManager = get()
        )
    }
    viewModel {
        ItemViewModel(
            getPagedItemsUseCase = get(),
            itemSyncDataSource = get(),
            itemsScheduler = get(),
            snackbarController = get(),
            stringProvider = get(),
            saveSearchQueryUseCase = get(),
            getSearchQueriesUseCase = get(),
            deleteSearchQueriesUseCase = get(),
            getUserUseCase = get(),
            adsConsentManager = get(),
        )
    }
    viewModel { (itemId: Long) ->
        ItemDetailsViewModel(
            getItemDetailsUseCase = get(),
            itemId = itemId,
        )
    }
    viewModel {
        SettingsViewModel(
            logoutUserUseCase = get(),
            getUserUseCase = get(),
            getUserPreferencesUseCase = get(),
            setThemeConfigUseCase = get(),
            setDynamicColorPreferenceUseCase = get(),
            setUseSystemColorsPreferenceUseCase = get(),
            permissionsController = get(),
            googleAuthClient = get(),
            snackbarController = get(),
            stringProvider = get(),
            systemDarkThemeObserver = get(),
            itemsScheduler = get(),
            itemSyncDataSource = get(),
            userEventController = get(),
            getActiveSubscriptionUseCase = get(),
            setSubscribedUseCase = get(),
            remoteConfig = get(),
            connectivityObserver = get(),
            adsConsentManager = get(),
        )
    }
    viewModel {
        DeleteAccountViewModel(
            deleteAccountUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            getUserUseCase = get(),
            getActiveSubscriptionUseCase = get(),
            stringProvider = get(),
            userEventController = get()
        )
    }
    viewModel {
        ChangePasswordViewModel(
            changePasswordUseCase = get(),
            snackbarController = get(),
            passwordValidator = get(),
            stringProvider = get(),
        )
    }
    viewModel { (email: String) ->
        ResetPasswordViewModel(
            email = email,
            requestPasswordResetUseCase = get(),
            snackbarController = get(),
            emailValidator = get(),
            stringProvider = get()
        )
    }
    viewModel {
        UpgradeViewModel(
            upgradeAccountUseCase = get(),
            upgradeWithGoogleUseCase = get(),
            getGoogleClientIdUseCase = get(),
            googleAuthClient = get(),
            snackbarController = get(),
            usernameValidator = get(),
            passwordValidator = get(),
            emailValidator = get(),
            stringProvider = get(),
            remoteConfig = get()
        )
    }
    viewModel {
        ConfirmEmailViewModel(
            checkEmailConfirmedUseCase = get(),
            getUserUseCase = get(),
            resendConfirmationUseCase = get(),
            snackbarController = get(),
            setEmailConfirmedUseCase = get(),
            stringProvider = get()
        )
    }
    viewModel { (serverId: Long, serverName: String?) ->
        ServerDetailsViewModel(
            getServerDetailsUseCase = get(),
            toggleFavouriteUseCase = get(),
            toggleSubscriptionUseCase = get(),
            getUserUseCase = get(),
            resendConfirmationUseCase = get(),
            permissionsController = get(),
            stringProvider = get(),
            serverName = serverName,
            serverId = serverId,
            clipboardHandler = get(),
            snackbarController = get(),
            shareHandler = get(),
            reviewRequester = get(),
            connectivityObserver = get()
        )
    }
    viewModel { (plan: SubscriptionPlan?) ->
        SubscriptionViewModel(
            billingRepository = get(),
            confirmPurchaseUseCase = get(),
            refreshUserUseCase = get(),
            getUserUseCase = get(),
            snackbarController = get(),
            stringProvider = get(),
            getActiveSubscriptionUseCase = get(),
            connectivityObserver = get(),
            initialPlan = plan
        )
    }
}

