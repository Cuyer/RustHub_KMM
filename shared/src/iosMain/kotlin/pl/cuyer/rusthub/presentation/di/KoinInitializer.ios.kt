package pl.cuyer.rusthub.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory
import pl.cuyer.rusthub.data.network.HttpClientFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailViewModel
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsViewModel
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ChangePasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.settings.SettingsViewModel
import pl.cuyer.rusthub.presentation.features.startup.StartupViewModel
import pl.cuyer.rusthub.domain.usecase.GetUserPreferencesUseCase
import pl.cuyer.rusthub.domain.usecase.SetThemeConfigUseCase
import pl.cuyer.rusthub.domain.usecase.SetDynamicColorPreferenceUseCase
import pl.cuyer.rusthub.util.AppCheckTokenProvider
import pl.cuyer.rusthub.util.ClipboardHandler
import pl.cuyer.rusthub.util.GoogleAuthClient
import pl.cuyer.rusthub.util.InAppUpdateManager
import pl.cuyer.rusthub.util.MessagingTokenScheduler
import pl.cuyer.rusthub.util.ReviewRequester
import pl.cuyer.rusthub.util.ShareHandler
import pl.cuyer.rusthub.util.StoreNavigator
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.SubscriptionSyncScheduler
import pl.cuyer.rusthub.util.SyncScheduler
import pl.cuyer.rusthub.util.ItemsScheduler
import pl.cuyer.rusthub.util.TokenRefresher
import pl.cuyer.rusthub.util.SystemDarkThemeObserver
import pl.cuyer.rusthub.util.PurchaseSyncScheduler
import pl.cuyer.rusthub.util.UserSyncScheduler
import pl.cuyer.rusthub.util.AdsConsentManager
import pl.cuyer.rusthub.data.billing.BillingRepositoryImpl
import pl.cuyer.rusthub.domain.repository.purchase.BillingRepository
import pl.cuyer.rusthub.domain.usecase.ConfirmPurchaseUseCase
import pl.cuyer.rusthub.domain.usecase.RefreshUserUseCase
import pl.cuyer.rusthub.presentation.features.subscription.SubscriptionViewModel
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.data.local.item.ItemSyncDataSourceImpl
import pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource
import pl.cuyer.rusthub.data.local.purchase.PurchaseSyncDataSourceImpl
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource
import pl.cuyer.rusthub.presentation.features.item.ItemViewModel
import pl.cuyer.rusthub.presentation.features.item.ItemDetailsViewModel
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.data.ads.NativeAdRepositoryImpl
import pl.cuyer.rusthub.domain.repository.ads.NativeAdRepository
import pl.cuyer.rusthub.domain.usecase.ads.GetNativeAdUseCase
import pl.cuyer.rusthub.domain.usecase.ads.ClearNativeAdsUseCase
import pl.cuyer.rusthub.presentation.features.ads.NativeAdViewModel

@Suppress("UNUSED_PARAMETER")
actual fun platformModule(passphrase: String): Module = module {
    single<RustHubDatabase> { DatabaseDriverFactory().create() }
    single { AppCheckTokenProvider() }
    single { HttpClientFactory(get(), get(), get(), get(), get()).create() }
    single { TokenRefresher() }
    single { ClipboardHandler() }
    single { ShareHandler() }
    single { SyncScheduler() }
    single { SubscriptionSyncScheduler() }
    single { MessagingTokenScheduler() }
    single { ItemsScheduler() }
    single { PurchaseSyncScheduler() }
    single { UserSyncScheduler() }
    single { BillingRepositoryImpl() } bind BillingRepository::class
    single { ItemSyncDataSourceImpl(get()) } bind ItemSyncDataSource::class
    single { PurchaseSyncDataSourceImpl(get()) } bind PurchaseSyncDataSource::class
    single { InAppUpdateManager() }
    single { ReviewRequester() }
    single { StoreNavigator() }
    single { UrlOpener() }
    single { EmailSender() }
    single { SystemDarkThemeObserver() }
    single { GoogleAuthClient() }
    single { StringProvider() }
    single { AdsConsentManager() }
    single<NativeAdRepository> { NativeAdRepositoryImpl() }
    factory { GetNativeAdUseCase(get()) }
    factory { ClearNativeAdsUseCase(get()) }
    factory { NativeAdViewModel(get(), get()) }
    factory {
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
    factory {
        ItemViewModel(
            getPagedItemsUseCase = get(),
            itemSyncDataSource = get(),
            itemsScheduler = get(),
            getUserUseCase = get(),
            adsConsentManager = get()
        )
    }
    factory { (itemId: Long) ->
        ItemDetailsViewModel(
            getItemDetailsUseCase = get(),
            itemId = itemId,
        )
    }
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
            setSubscribedUseCase = get(),
            connectivityObserver = get()
        )
    }
    factory {
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
    factory { (plan: SubscriptionPlan?) ->
        SubscriptionViewModel(
            billingRepository = get(),
            confirmPurchaseUseCase = get(),
            refreshUserUseCase = get(),
            getUserUseCase = get(),
            getActiveSubscriptionUseCase = get(),
            snackbarController = get(),
            stringProvider = get(),
            connectivityObserver = get(),
            initialPlan = plan
        )
    }
}
