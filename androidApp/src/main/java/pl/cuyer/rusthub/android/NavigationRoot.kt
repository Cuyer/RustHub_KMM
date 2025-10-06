package pl.cuyer.rusthub.android

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
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
import pl.cuyer.rusthub.android.feature.server.ServerDetailsScreen
import pl.cuyer.rusthub.android.feature.server.ServerScreen
import pl.cuyer.rusthub.android.feature.settings.ChangePasswordScreen
import pl.cuyer.rusthub.android.feature.settings.DeleteAccountScreen
import pl.cuyer.rusthub.android.feature.settings.PrivacyPolicyScreen
import pl.cuyer.rusthub.android.feature.settings.SettingsScreen
import pl.cuyer.rusthub.android.feature.subscription.SubscriptionScreen
import pl.cuyer.rusthub.android.feature.raid.RaidSchedulerScreen
import pl.cuyer.rusthub.android.feature.raid.RaidFormScreen
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.navigation.BottomNavKey
import pl.cuyer.rusthub.android.navigation.bottomNavItems
import pl.cuyer.rusthub.android.navigation.navigateBottomBar
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.Urls
import pl.cuyer.rusthub.common.user.UserEvent
import pl.cuyer.rusthub.common.user.UserEventController
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
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerViewModel
import pl.cuyer.rusthub.presentation.features.settings.SettingsViewModel
import pl.cuyer.rusthub.presentation.features.subscription.SubscriptionViewModel
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerViewModel
import pl.cuyer.rusthub.presentation.features.raid.RaidFormViewModel
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
import pl.cuyer.rusthub.presentation.navigation.RaidScheduler
import pl.cuyer.rusthub.presentation.navigation.RaidForm
import pl.cuyer.rusthub.presentation.navigation.PrivacyPolicy
import pl.cuyer.rusthub.presentation.navigation.ResetPassword
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.Settings
import pl.cuyer.rusthub.presentation.navigation.Subscription
import pl.cuyer.rusthub.presentation.navigation.Terms
import pl.cuyer.rusthub.presentation.navigation.UpgradeAccount
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun NavigationRoot(startDestination: () -> NavKey) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(flow = SnackbarController.events, snackbarHostState) { event ->
        scope.launch {
            keyboardController?.hide()
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = when (event.duration) {
                    Duration.SHORT -> SnackbarDuration.Short
                    Duration.LONG -> SnackbarDuration.Long
                    Duration.INDEFINITE -> SnackbarDuration.Indefinite
                }
            )
            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    val backStack = rememberNavBackStack(startDestination())
    ObserveAsEvents(flow = UserEventController.events, key1 = backStack) { event ->
        if (event is UserEvent.LoggedOut) {
            backStack.clear()
            backStack.add(Onboarding)
        }
    }

    val onPop: () -> Unit = { backStack.removeLastOrNull() }
    val onPopWhile: ((NavKey?) -> Boolean) -> Unit = { predicate ->
        while (predicate(backStack.lastOrNull())) {
            backStack.removeLastOrNull()
        }
    }
    val onClear: () -> Unit = { backStack.clear() }
    val onBack: (Int) -> Unit = { keysToRemove -> repeat(keysToRemove) { onPop() } }
    val onBottomBarClick: (BottomNavKey) -> Unit = { navigateBottomBar(backStack, it) }


    val onNavigateSingleTop: (NavKey) -> Unit = singleTop@ { destination ->
        if (backStack.lastOrNull() == destination) {
            return@singleTop
        }

        backStack.removeEntriesFor(destination)
        backStack.add(destination)
    }

    val onNavigate: (NavKey) -> Unit = onNavigateSingleTop

    LaunchedEffect(backStack.lastOrNull()) { snackbarHostState.currentSnackbarData?.dismiss() }

    if (bottomNavItems.any { it.isInHierarchy(backStack.lastOrNull()) }) {
        NavigationSuiteScaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .safeDrawingPadding(),
            navigationItems = { BottomBarItems(backStack.lastOrNull(), onBottomBarClick) },
            content = {
                AppScaffold(
                    snackbarHostState = snackbarHostState,
                    backStack = { backStack },
                    onBack = onBack,
                    onNavigate = onNavigate,
                    onNavigateUp = onPop,
                    onPopWhile = onPopWhile,
                    onClear = onClear
                )
            }
        )
    } else {
        AppScaffold(
            snackbarHostState = snackbarHostState,
            backStack = { backStack },
            onBack = onBack,
            onNavigate = onNavigate,
            onNavigateUp = onPop,
            onPopWhile = onPopWhile,
            onClear = onClear,
            modifier = Modifier
                .safeDrawingPadding()
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun AppScaffold(
    snackbarHostState: SnackbarHostState,
    backStack: () -> List<NavKey>,
    onBack: (Int) -> Unit,
    onNavigate: (NavKey) -> Unit,
    onNavigateUp: () -> Unit,
    onPopWhile: ((NavKey?) -> Boolean) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { contentPadding ->
            val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
            val decoratedEntries =
                rememberDecoratedNavEntries(
                    backStack = backStack(),
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider provider@{
                        fun registerAuthEntries() = with(this@provider) {
                            entry<Onboarding> {
                                val viewModel = koinViewModel<OnboardingViewModel>()
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                OnboardingScreen(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    uiEvent = viewModel.uiEvent,
                                    onNavigate = { dest ->
                                        if (dest is ServerList) onClear()
                                        onNavigate(dest)
                                    }
                                )
                            }
                            entry<Credentials> { key ->
                                val viewModel: CredentialsViewModel =
                                    koinViewModel { parametersOf(key.email, key.exists, key.provider) }
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                CredentialsScreen(
                                    state = state,
                                    uiEvent = viewModel.uiEvent,
                                    onAction = viewModel::onAction,
                                    onNavigate = { dest ->
                                        if (dest is ServerList) onClear()
                                        if (dest is ConfirmEmail) onNavigateUp()
                                        onNavigate(dest)
                                    },
                                    onNavigateUp = onNavigateUp
                                )
                            }
                            entry<ConfirmEmail> {
                                val viewModel = koinViewModel<ConfirmEmailViewModel>()
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                ConfirmEmailScreen(
                                    uiEvent = viewModel.uiEvent,
                                    state = state,
                                    onAction = viewModel::onAction,
                                    onNavigate = { dest ->
                                        onClear()
                                        onNavigate(dest)
                                    },
                                    onNavigateUp = onNavigateUp
                                )
                            }
                            entry<ResetPassword> { key ->
                                val viewModel: ResetPasswordViewModel =
                                    koinViewModel { parametersOf(key.email) }
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                ResetPasswordScreen(
                                    onNavigateUp = onNavigateUp,
                                    uiEvent = viewModel.uiEvent,
                                    state = state,
                                    onAction = viewModel::onAction
                                )
                            }
                            entry<ChangePassword> {
                                val viewModel = koinViewModel<ChangePasswordViewModel>()
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                ChangePasswordScreen(
                                    onNavigateUp = onNavigateUp,
                                    uiEvent = viewModel.uiEvent,
                                    state = state,
                                    onAction = viewModel::onAction
                                )
                            }
                        }

                        fun registerServerEntries() = with(this@provider) {
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
                                    onNavigate = { dest -> onNavigate(dest) },
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
                                    onNavigate = { dest -> onNavigate(dest) },
                                    onNavigateUp = onNavigateUp,
                                    showAds = showAds,
                                    adState = adState,
                                    onAdAction = adViewModel::onAction
                                )
                            }
                        }

                        fun registerItemEntries() = with(this@provider) {
                            entry<ItemList>(metadata = ListDetailSceneStrategy.listPane()) {
                                val viewModel = koinViewModel<ItemViewModel>()
                                val adViewModel = koinViewModel<NativeAdViewModel>()
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                val paging = viewModel.paging.collectAsLazyPagingItems()
                                val showAds by viewModel.showAds.collectAsStateWithLifecycle()
                                val adState = adViewModel.state.collectAsStateWithLifecycle()
                                ItemScreen(
                                    state = state,
                                    uiEvent = viewModel.uiEvent,
                                    onAction = viewModel::onAction,
                                    pagedList = paging,
                                    onNavigate = { dest -> onNavigate(dest) },
                                    showAds = showAds,
                                    adState = adState,
                                    onAdAction = adViewModel::onAction
                                )
                            }
                            entry<ItemDetails>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
                                val viewModel: ItemDetailsViewModel = koinViewModel(
                                    key = key.id.toString()
                                ) { parametersOf(key.id, key.name) }
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                ItemDetailsScreen(
                                    state = state,
                                    onNavigateUp = { onPopWhile { it is ItemDetails } },
                                    onRefresh = viewModel::refresh,
                                )
                            }
                        }

                        fun registerMonumentEntries() = with(this@provider) {
                            entry<MonumentList>(metadata = ListDetailSceneStrategy.listPane()) {
                                val viewModel = koinViewModel<MonumentViewModel>()
                                val adViewModel = koinViewModel<NativeAdViewModel>()
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                val paging = viewModel.paging.collectAsLazyPagingItems()
                                val showAds by viewModel.showAds.collectAsStateWithLifecycle()
                                val adState = adViewModel.state.collectAsStateWithLifecycle()
                                MonumentScreen(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    pagedList = paging,
                                    uiEvent = viewModel.uiEvent,
                                    onNavigate = { dest -> onNavigate(dest) },
                                    showAds = showAds,
                                    adState = adState,
                                    onAdAction = adViewModel::onAction
                                )
                            }
                            entry<MonumentDetails>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
                                val viewModel: MonumentDetailsViewModel = koinViewModel(
                                    key = key.slug
                                ) { parametersOf(key.slug) }
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                MonumentDetailsScreen(
                                    state = state,
                                    onNavigateUp = { onPopWhile { it is MonumentDetails } },
                                )
                            }
                        }

                        fun registerRaidEntries() = with(this@provider) {
                            entry<RaidScheduler>(metadata = ListDetailSceneStrategy.listPane()) {
                                val viewModel = koinViewModel<RaidSchedulerViewModel>()
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                RaidSchedulerScreen(
                                    onNavigate = { dest -> onNavigate(dest) },
                                    state = state,
                                    onAction = viewModel::onAction,
                                    uiEvent = viewModel.uiEvent
                                )
                            }
                            entry<RaidForm>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
                                val viewModel: RaidFormViewModel = koinViewModel(
                                    key = key.raid?.id ?: "new"
                                ) { parametersOf(key.raid) }
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                RaidFormScreen(
                                    onNavigateUp = { onPopWhile { it is RaidForm } },
                                    state = state,
                                    onAction = viewModel::onAction,
                                    uiEvent = viewModel.uiEvent
                                )
                            }
                        }

                        fun registerSettingsEntries() = with(this@provider) {
                            entry<Settings> {
                                val viewModel = koinViewModel<SettingsViewModel>()
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                SettingsScreen(
                                    state = state,
                                    uiEvent = viewModel.uiEvent,
                                    onAction = viewModel::onAction,
                                    onNavigate = { dest -> onNavigate(dest) }
                                )
                            }
                            entry<DeleteAccount> {
                                val viewModel = koinViewModel<DeleteAccountViewModel>()
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                DeleteAccountScreen(
                                    onNavigateUp = onNavigateUp,
                                    state = state,
                                    onAction = viewModel::onAction
                                )
                            }
                            entry<UpgradeAccount> {
                                val viewModel = koinViewModel<UpgradeViewModel>()
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                UpgradeAccountScreen(
                                    onNavigateUp = onNavigateUp,
                                    uiEvent = viewModel.uiEvent,
                                    state = state,
                                    onAction = viewModel::onAction
                                )
                            }
                        }

                        fun registerLegalEntries() = with(this@provider) {
                            entry<PrivacyPolicy> {
                                PrivacyPolicyScreen(
                                    url = Urls.PRIVACY_POLICY_URL,
                                    onNavigateUp = onNavigateUp
                                )
                            }
                            entry<Terms> {
                                PrivacyPolicyScreen(
                                    url = Urls.TERMS_URL,
                                    title = stringResource(SharedRes.strings.terms_conditions),
                                    onNavigateUp = onNavigateUp
                                )
                            }
                            entry<About> {
                                AboutScreen(
                                    onNavigateUp = onNavigateUp
                                )
                            }
                        }

                        fun registerSubscriptionEntry() = with(this@provider) {
                            entry<Subscription> { key ->
                                val viewModel: SubscriptionViewModel = koinViewModel {
                                    parametersOf(key.plan)
                                }
                                val state = viewModel.state.collectAsStateWithLifecycle()
                                SubscriptionScreen(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    uiEvent = viewModel.uiEvent,
                                    onNavigateUp = onNavigateUp,
                                    onPrivacyPolicy = { onNavigate(PrivacyPolicy) },
                                    onTerms = { onNavigate(Terms) }
                                )
                            }
                        }

                        registerAuthEntries()
                        registerServerEntries()
                        registerItemEntries()
                        registerMonumentEntries()
                        registerRaidEntries()
                        registerSettingsEntries()
                        registerLegalEntries()
                        registerSubscriptionEntry()
                    },
                )

            NavDisplay(
                entries = decoratedEntries,
                modifier = Modifier
                    .padding(contentPadding)
                    .consumeWindowInsets(contentPadding),
                sceneStrategy = listDetailStrategy,
                transitionSpec = {
                    fadeIn(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    ) togetherWith
                            fadeOut(
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                )
                            )
                },
                popTransitionSpec = {
                    fadeIn(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    ) togetherWith
                            fadeOut(
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                )
                            )
                },
                onBack = { onBack(1) },
            )
        }
    )
}

private fun MutableList<NavKey>.removeEntriesFor(destination: NavKey) {
    when (destination) {
        is ServerDetails -> removeFromEndWhile { it is ServerDetails }
        is ItemDetails -> removeFromEndWhile { it is ItemDetails }
        is MonumentDetails -> removeFromEndWhile { it is MonumentDetails }
        is RaidForm -> removeFromEndWhile { it is RaidForm }
        else -> removeAll { it == destination }
    }
}

private fun MutableList<NavKey>.removeFromEndWhile(predicate: (NavKey) -> Boolean) {
    while (isNotEmpty() && predicate(last())) {
        removeLast()
    }
}

@Composable
private fun BottomBarItems(current: NavKey?, onNavigate: (BottomNavKey) -> Unit) {
    bottomNavItems.forEach { item ->
        NavigationSuiteItem(
            selected = item.isInHierarchy(current),
            onClick = { onNavigate(item) },
            icon = {
                Icon(
                    item.icon,
                    contentDescription = stringResource(item.label)
                )
            },
            label = { Text(stringResource(item.label)) }
        )
    }
}