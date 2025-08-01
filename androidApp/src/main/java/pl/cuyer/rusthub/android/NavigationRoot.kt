package pl.cuyer.rusthub.android

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.feature.auth.ConfirmEmailScreen
import pl.cuyer.rusthub.android.feature.auth.CredentialsScreen
import pl.cuyer.rusthub.android.feature.auth.ResetPasswordScreen
import pl.cuyer.rusthub.android.feature.auth.UpgradeAccountScreen
import pl.cuyer.rusthub.android.feature.item.ItemDetailsScreen
import pl.cuyer.rusthub.android.feature.item.ItemScreen
import pl.cuyer.rusthub.android.feature.onboarding.OnboardingScreen
import pl.cuyer.rusthub.android.feature.server.ServerDetailsScreen
import pl.cuyer.rusthub.android.feature.server.ServerScreen
import pl.cuyer.rusthub.android.feature.item.ItemScreen
import pl.cuyer.rusthub.android.feature.item.ItemDetailsScreen
import pl.cuyer.rusthub.presentation.features.item.ItemDetailsViewModel
import pl.cuyer.rusthub.android.feature.settings.ChangePasswordScreen
import pl.cuyer.rusthub.android.feature.settings.DeleteAccountScreen
import pl.cuyer.rusthub.android.feature.settings.PrivacyPolicyScreen
import pl.cuyer.rusthub.android.feature.about.AboutScreen
import pl.cuyer.rusthub.android.feature.settings.SettingsScreen
import pl.cuyer.rusthub.android.feature.subscription.SubscriptionScreen
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.navigation.bottomNavItems
import pl.cuyer.rusthub.android.navigation.navigateBottomBar
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.Urls
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailViewModel
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsViewModel
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ChangePasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeViewModel
import pl.cuyer.rusthub.presentation.features.item.ItemState
import pl.cuyer.rusthub.presentation.features.item.ItemViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerViewModel
import pl.cuyer.rusthub.presentation.features.settings.SettingsViewModel
import pl.cuyer.rusthub.presentation.features.subscription.SubscriptionViewModel
import pl.cuyer.rusthub.presentation.features.ads.NativeAdViewModel
import pl.cuyer.rusthub.presentation.navigation.ChangePassword
import pl.cuyer.rusthub.presentation.navigation.ConfirmEmail
import pl.cuyer.rusthub.presentation.navigation.Credentials
import pl.cuyer.rusthub.presentation.navigation.DeleteAccount
import pl.cuyer.rusthub.presentation.navigation.ItemDetails
import pl.cuyer.rusthub.presentation.navigation.ItemList
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.PrivacyPolicy
import pl.cuyer.rusthub.presentation.navigation.ResetPassword
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.Settings
import pl.cuyer.rusthub.presentation.navigation.Subscription
import pl.cuyer.rusthub.presentation.navigation.Terms
import pl.cuyer.rusthub.presentation.navigation.About
import pl.cuyer.rusthub.presentation.navigation.UpgradeAccount
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.common.user.UserEvent
import pl.cuyer.rusthub.common.user.UserEventController

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun NavigationRoot(startDestination: NavKey) {
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

    val backStack = rememberNavBackStack(startDestination)
    ObserveAsEvents(flow = UserEventController.events, key1 = backStack) { event ->
        if (event is UserEvent.LoggedOut) {
            backStack.clear()
            backStack.add(Onboarding)
        }
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>()

    val current = backStack.lastOrNull()
    LaunchedEffect(current) { snackbarHostState.currentSnackbarData?.dismiss() }
    val showNav = bottomNavItems.any { it.isInHierarchy(current) }

    if (showNav) {
        NavigationSuiteScaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .safeDrawingPadding(),
            navigationItems = { BottomBarItems(current, backStack) },
            content = {
                AppScaffold(
                    snackbarHostState = snackbarHostState,
                    backStack = backStack,
                    listDetailStrategy = listDetailStrategy
                )
            }
        )
    } else {
        AppScaffold(
            snackbarHostState = snackbarHostState,
            backStack = backStack,
            listDetailStrategy = listDetailStrategy,
            modifier = Modifier
                .safeDrawingPadding()
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun AppScaffold(
    snackbarHostState: SnackbarHostState,
    backStack: MutableList<NavKey>,
    listDetailStrategy: ListDetailSceneStrategy<Any>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { contentPadding ->
            NavDisplay(
                modifier = Modifier
                    .padding(contentPadding)
                    .consumeWindowInsets(contentPadding),
                backStack = backStack,
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
                entryDecorators = listOf(
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                onBack = { keysToRemove ->
                    repeat(keysToRemove) { backStack.removeLastOrNull() }
                },
                entryProvider = entryProvider {
                    entry<Onboarding> {
                        val viewModel = koinViewModel<OnboardingViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        OnboardingScreen(
                            state = state,
                            onAction = viewModel::onAction,
                            uiEvent = viewModel.uiEvent,
                            onNavigate = { dest ->
                                backStack.apply {
                                    if (dest is ServerList) clear()
                                    add(dest)
                                }
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
                                if (dest is ServerList) backStack.clear()
                                if (dest is ConfirmEmail) backStack.removeLastOrNull()
                                backStack.add(dest)
                            },
                            onNavigateUp = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
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
                            onNavigate = { dest ->
                                backStack.add(dest)
                            },
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
                        ServerDetailsScreen(
                            state = state,
                            uiEvent = viewModel.uiEvent,
                            onAction = viewModel::onAction,
                            onNavigate = { dest -> backStack.add(dest) },
                            onNavigateUp = { backStack.removeLastOrNull() }
                        )
                    }
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
                            onNavigate = { dest -> backStack.add(dest) },
                            showAds = showAds,
                            adState = adState,
                            onAdAction = adViewModel::onAction
                        )
                    }
                    entry<ItemDetails>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
                        val viewModel: ItemDetailsViewModel = koinViewModel(
                            key = key.id.toString()
                        ) { parametersOf(key.id) }
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        ItemDetailsScreen(
                            state = state,
                            onNavigateUp = {
                                while (backStack.lastOrNull() is ItemDetails) {
                                    backStack.removeLastOrNull()
                                }
                            },
                        )
                    }
                    entry<Settings> {
                        val viewModel = koinViewModel<SettingsViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        SettingsScreen(
                            state = state,
                            uiEvent = viewModel.uiEvent,
                            onAction = viewModel::onAction,
                            onNavigate = { dest ->
                                backStack.add(dest)
                            }
                        )
                    }
                    entry<DeleteAccount> {
                        val viewModel = koinViewModel<DeleteAccountViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        DeleteAccountScreen(
                            onNavigateUp = { backStack.removeLastOrNull() },
                            state = state,
                            onAction = viewModel::onAction
                        )
                    }
                    entry<UpgradeAccount> {
                        val viewModel = koinViewModel<UpgradeViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        UpgradeAccountScreen(
                            onNavigateUp = { backStack.removeLastOrNull() },
                            uiEvent = viewModel.uiEvent,
                            state = state,
                            onAction = viewModel::onAction
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
                                backStack.clear()
                                backStack.add(dest)
                            },
                            onNavigateUp = { backStack.removeLastOrNull() }
                        )
                    }
                    entry<ResetPassword> { key ->
                        val viewModel: ResetPasswordViewModel =
                            koinViewModel { parametersOf(key.email) }
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        ResetPasswordScreen(
                            onNavigateUp = { backStack.removeLastOrNull() },
                            uiEvent = viewModel.uiEvent,
                            state = state,
                            onAction = viewModel::onAction
                        )
                    }
                    entry<ChangePassword> {
                        val viewModel = koinViewModel<ChangePasswordViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        ChangePasswordScreen(
                            onNavigateUp = { backStack.removeLastOrNull() },
                            uiEvent = viewModel.uiEvent,
                            state = state,
                            onAction = viewModel::onAction
                        )
                    }

                    entry<PrivacyPolicy> {
                        PrivacyPolicyScreen(
                            url = Urls.PRIVACY_POLICY_URL,
                            onNavigateUp = { backStack.removeLastOrNull() }
                        )
                    }
                    entry<Terms> {
                        PrivacyPolicyScreen(
                            url = Urls.TERMS_URL,
                            title = stringResource(SharedRes.strings.terms_conditions),
                            onNavigateUp = { backStack.removeLastOrNull() }
                        )
                    }
                    entry<About> {
                        AboutScreen(
                            onNavigateUp = { backStack.removeLastOrNull() }
                        )
                    }
                    entry<Subscription> { key ->
                        val viewModel: SubscriptionViewModel = koinViewModel {
                            parametersOf(key.plan)
                        }
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        SubscriptionScreen(
                            state = state,
                            onAction = viewModel::onAction,
                            uiEvent = viewModel.uiEvent,
                            onNavigateUp = { backStack.removeLastOrNull() },
                            onPrivacyPolicy = { backStack.add(PrivacyPolicy) },
                            onTerms = { backStack.add(Terms) }
                        )
                    }
                },
                sceneStrategy = listDetailStrategy
            )
        }
    )
}

@Composable
private fun BottomBarItems(current: NavKey?, backStack: MutableList<NavKey>) {
    bottomNavItems.forEach { item ->
        NavigationSuiteItem(
            selected = item.isInHierarchy(current),
            onClick = { navigateBottomBar(backStack, item) },
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
