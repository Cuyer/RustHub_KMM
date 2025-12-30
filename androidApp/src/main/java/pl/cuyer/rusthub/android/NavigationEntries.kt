@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package pl.cuyer.rusthub.android

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.paging.compose.collectAsLazyPagingItems
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.feature.about.AboutScreen
import pl.cuyer.rusthub.android.feature.auth.ConfirmEmailScreen
import pl.cuyer.rusthub.android.feature.auth.CredentialsScreen
import pl.cuyer.rusthub.android.feature.auth.ResetPasswordScreen
import pl.cuyer.rusthub.android.feature.auth.UpgradeAccountScreen
import pl.cuyer.rusthub.android.feature.item.ItemDetailsScreen
import pl.cuyer.rusthub.android.feature.item.ItemScreen
import pl.cuyer.rusthub.android.feature.monument.MonumentDetailsScreen
import pl.cuyer.rusthub.android.feature.monument.MonumentScreen
import pl.cuyer.rusthub.android.feature.onboarding.OnboardingScreen
import pl.cuyer.rusthub.android.feature.raid.RaidFormScreen
import pl.cuyer.rusthub.android.feature.raid.RaidSchedulerScreen
import pl.cuyer.rusthub.android.feature.server.ServerDetailsScreen
import pl.cuyer.rusthub.android.feature.server.ServerScreen
import pl.cuyer.rusthub.android.feature.settings.ChangePasswordScreen
import pl.cuyer.rusthub.android.feature.settings.DeleteAccountScreen
import pl.cuyer.rusthub.android.feature.settings.PrivacyPolicyScreen
import pl.cuyer.rusthub.android.feature.settings.SettingsScreen
import pl.cuyer.rusthub.android.feature.subscription.SubscriptionScreen
import pl.cuyer.rusthub.android.navigation.Navigator
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.Urls
import pl.cuyer.rusthub.presentation.features.ads.NativeAdViewModel
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailViewModel
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsViewModel
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ChangePasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeViewModel
import pl.cuyer.rusthub.presentation.features.item.ItemDetailsViewModel
import pl.cuyer.rusthub.presentation.features.item.ItemViewModel
import pl.cuyer.rusthub.presentation.features.monument.MonumentDetailsViewModel
import pl.cuyer.rusthub.presentation.features.monument.MonumentViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.raid.RaidFormViewModel
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerViewModel
import pl.cuyer.rusthub.presentation.features.settings.SettingsViewModel
import pl.cuyer.rusthub.presentation.features.subscription.SubscriptionViewModel
import pl.cuyer.rusthub.presentation.navigation.About
import pl.cuyer.rusthub.presentation.navigation.ChangePassword
import pl.cuyer.rusthub.presentation.navigation.ConfirmEmail
import pl.cuyer.rusthub.presentation.navigation.Credentials
import pl.cuyer.rusthub.presentation.navigation.DeleteAccount
import pl.cuyer.rusthub.presentation.navigation.ItemDetails
import pl.cuyer.rusthub.presentation.navigation.ItemList
import pl.cuyer.rusthub.presentation.navigation.MonumentDetails
import pl.cuyer.rusthub.presentation.navigation.MonumentList
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.PrivacyPolicy
import pl.cuyer.rusthub.presentation.navigation.RaidForm
import pl.cuyer.rusthub.presentation.navigation.RaidScheduler
import pl.cuyer.rusthub.presentation.navigation.ResetPassword
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.Settings
import pl.cuyer.rusthub.presentation.navigation.Subscription
import pl.cuyer.rusthub.presentation.navigation.Terms
import pl.cuyer.rusthub.presentation.navigation.UpgradeAccount

internal fun EntryProviderScope<NavKey>.registerAuthEntries(
    navigator: Navigator,
) {
    entry<Onboarding> {
        val viewModel = koinViewModel<OnboardingViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        OnboardingScreen(
            state = state,
            onAction = viewModel::onAction,
            uiEvent = viewModel.uiEvent,
            onNavigate = navigator::navigate
        )
    }
    entry<Credentials> { key ->
        val viewModel: CredentialsViewModel =
            koinViewModel {
                parametersOf(
                    key.email,
                    key.exists,
                    key.provider
                )
            }
        val state = viewModel.state.collectAsStateWithLifecycle()
        CredentialsScreen(
            state = state,
            uiEvent = viewModel.uiEvent,
            onAction = viewModel::onAction,
            onNavigate = navigator::navigate,
            onNavigateUp = { navigator.goBack() }
        )
    }
    entry<ConfirmEmail> {
        val viewModel = koinViewModel<ConfirmEmailViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        ConfirmEmailScreen(
            uiEvent = viewModel.uiEvent,
            state = state,
            onAction = viewModel::onAction,
            onNavigate = navigator::navigate,
            onNavigateUp = { navigator.goBack() }
        )
    }
    entry<ResetPassword> { key ->
        val viewModel: ResetPasswordViewModel =
            koinViewModel { parametersOf(key.email) }
        val state = viewModel.state.collectAsStateWithLifecycle()
        ResetPasswordScreen(
            onNavigateUp = { navigator.goBack() },
            uiEvent = viewModel.uiEvent,
            state = state,
            onAction = viewModel::onAction
        )
    }
    entry<ChangePassword> {
        val viewModel = koinViewModel<ChangePasswordViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        ChangePasswordScreen(
            onNavigateUp = { navigator.goBack() },
            uiEvent = viewModel.uiEvent,
            state = state,
            onAction = viewModel::onAction
        )
    }
}

internal fun EntryProviderScope<NavKey>.registerServerEntries(
    navigator: Navigator,
) {
    entry<ServerList>(metadata = ListDetailSceneStrategy.listPane()) {
        val viewModel = koinViewModel<ServerViewModel>()
        val adViewModel = koinViewModel<NativeAdViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        val paging = viewModel.paging.collectAsLazyPagingItems()
        val showAds by viewModel.showAds.collectAsStateWithLifecycle()
        val adState = adViewModel.state.collectAsStateWithLifecycle()
        ServerScreen(
            state = state,
            uiEvent = viewModel.uiEvent,
            onAction = viewModel::onAction,
            pagedList = paging,
            onNavigate = navigator::navigate,
            showAds = showAds,
            adState = adState,
            onAdAction = adViewModel::onAction
        )
    }
    entry<ServerDetails>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
        val viewModel: ServerDetailsViewModel = koinViewModel(
            key = key.id.toString()
        ) { parametersOf(key.id, key.name) }
        val state = viewModel.state.collectAsStateWithLifecycle()
        val showAds by viewModel.showAds.collectAsStateWithLifecycle()
        val adViewModel = koinViewModel<NativeAdViewModel>()
        val adState = adViewModel.state.collectAsStateWithLifecycle()
        ServerDetailsScreen(
            state = state,
            uiEvent = viewModel.uiEvent,
            onAction = viewModel::onAction,
            onNavigate = navigator::navigate,
            onNavigateUp = { navigator.goBack() },
            showAds = showAds,
            adState = adState,
            onAdAction = adViewModel::onAction
        )
    }
}

internal fun EntryProviderScope<NavKey>.registerItemEntries(
    navigator: Navigator,
) {
    entry<ItemList>(metadata = ListDetailSceneStrategy.listPane()) {
        val viewModel = koinViewModel<ItemViewModel>()
        val adViewModel = koinViewModel<NativeAdViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        val paging = viewModel.paging.collectAsLazyPagingItems()
        val showAds by viewModel.showAds.collectAsStateWithLifecycle()
        val adState = adViewModel.state.collectAsStateWithLifecycle()
        ItemScreen(
            state = state,
            pagedList = paging,
            uiEvent = viewModel.uiEvent,
            onAction = viewModel::onAction,
            onNavigate = navigator::navigate,
            showAds = showAds,
            adState = adState,
            onAdAction = adViewModel::onAction
        )
    }
    entry<ItemDetails>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
        val viewModel: ItemDetailsViewModel = koinViewModel(key = key.id.toString()) {
            parametersOf(key.id)
        }
        val state = viewModel.state.collectAsStateWithLifecycle()
        ItemDetailsScreen(
            state = state,
            onNavigateUp = { navigator.goBack() },
            onRefresh = viewModel::refresh
        )
    }
}

internal fun EntryProviderScope<NavKey>.registerMonumentEntries(
    navigator: Navigator,
) {
    entry<MonumentList>(metadata = ListDetailSceneStrategy.listPane()) {
        val viewModel = koinViewModel<MonumentViewModel>()
        val adViewModel = koinViewModel<NativeAdViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        val paging = viewModel.paging.collectAsLazyPagingItems()
        val showAds by viewModel.showAds.collectAsStateWithLifecycle()
        val adState = adViewModel.state.collectAsStateWithLifecycle()
        MonumentScreen(
            state = state,
            uiEvent = viewModel.uiEvent,
            onAction = viewModel::onAction,
            pagedList = paging,
            onNavigate = navigator::navigate,
            showAds = showAds,
            adState = adState,
            onAdAction = adViewModel::onAction
        )
    }
    entry<MonumentDetails>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
        val viewModel: MonumentDetailsViewModel =
            koinViewModel(key = key.slug) { parametersOf(key.slug) }
        val state = viewModel.state.collectAsStateWithLifecycle()
        MonumentDetailsScreen(
            state = state,
            onNavigateUp = { navigator.goBack() },
        )
    }
}

internal fun EntryProviderScope<NavKey>.registerRaidEntries(
    navigator: Navigator,
) {
    entry<RaidScheduler>(metadata = ListDetailSceneStrategy.listPane()) {
        val viewModel = koinViewModel<RaidSchedulerViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        RaidSchedulerScreen(
            state = state,
            uiEvent = viewModel.uiEvent,
            onAction = viewModel::onAction,
            onNavigate = navigator::navigate
        )
    }
    entry<RaidForm>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
        val viewModel: RaidFormViewModel = koinViewModel(key = key.raid?.id ?: "new") { parametersOf(key.raid) }
        val state = viewModel.state.collectAsStateWithLifecycle()
        RaidFormScreen(
            state = state,
            uiEvent = viewModel.uiEvent,
            onAction = viewModel::onAction,
            onNavigateUp = { navigator.goBack() }
        )
    }
}

internal fun EntryProviderScope<NavKey>.registerSettingsEntries(
    navigator: Navigator,
) {
    entry<Settings> {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        SettingsScreen(
            state = state,
            uiEvent = viewModel.uiEvent,
            onAction = viewModel::onAction,
            onNavigate = navigator::navigate
        )
    }
    entry<DeleteAccount> {
        val viewModel = koinViewModel<DeleteAccountViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        DeleteAccountScreen(
            state = state,
            onAction = viewModel::onAction,
            onNavigateUp = { navigator.goBack() }
        )
    }
    entry<UpgradeAccount> {
        val viewModel = koinViewModel<UpgradeViewModel>()
        val state = viewModel.state.collectAsStateWithLifecycle()
        UpgradeAccountScreen(
            state = state,
            onAction = viewModel::onAction,
            uiEvent = viewModel.uiEvent,
            onNavigateUp = { navigator.goBack() }
        )
    }
}

internal fun EntryProviderScope<NavKey>.registerLegalEntries(
    navigator: Navigator,
) {
    entry<PrivacyPolicy> {
        PrivacyPolicyScreen(
            url = Urls.PRIVACY_POLICY_URL,
            onNavigateUp = { navigator.goBack() }
        )
    }
    entry<Terms> {
        PrivacyPolicyScreen(
            url = Urls.TERMS_URL,
            title = stringResource(SharedRes.strings.terms_conditions),
            onNavigateUp = { navigator.goBack() }
        )
    }
    entry<About> {
        AboutScreen(
            onNavigateUp = { navigator.goBack() }
        )
    }
}

internal fun EntryProviderScope<NavKey>.registerSubscriptionEntry(
    navigator: Navigator,
) {
    entry<Subscription> { key ->
        val viewModel: SubscriptionViewModel = koinViewModel {
            parametersOf(key.plan)
        }
        val state = viewModel.state.collectAsStateWithLifecycle()
        SubscriptionScreen(
            state = state,
            onAction = viewModel::onAction,
            uiEvent = viewModel.uiEvent,
            onNavigateUp = { navigator.goBack() },
            onPrivacyPolicy = { navigator.navigate(PrivacyPolicy) },
            onTerms = { navigator.navigate(Terms) }
        )
    }
}

@Composable
internal fun rememberNavigationEntryProvider(
    navigator: Navigator,
): (NavKey) -> androidx.navigation3.runtime.NavEntry<NavKey> {
    return entryProvider {
        registerAuthEntries(navigator)
        registerServerEntries(navigator)
        registerItemEntries(navigator)
        registerMonumentEntries(navigator)
        registerRaidEntries(navigator)
        registerSettingsEntries(navigator)
        registerLegalEntries(navigator)
        registerSubscriptionEntry(navigator)
    }
}
