package pl.cuyer.rusthub.android

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.feature.auth.ConfirmEmailScreen
import pl.cuyer.rusthub.android.feature.auth.CredentialsScreen
import pl.cuyer.rusthub.android.feature.auth.ResetPasswordScreen
import pl.cuyer.rusthub.android.feature.auth.UpgradeAccountScreen
import pl.cuyer.rusthub.android.feature.onboarding.OnboardingScreen
import pl.cuyer.rusthub.android.feature.server.ServerDetailsScreen
import pl.cuyer.rusthub.android.feature.server.ServerScreen
import pl.cuyer.rusthub.android.feature.settings.ChangePasswordScreen
import pl.cuyer.rusthub.android.feature.settings.DeleteAccountScreen
import pl.cuyer.rusthub.android.feature.settings.PrivacyPolicyScreen
import pl.cuyer.rusthub.android.feature.settings.SettingsScreen
import pl.cuyer.rusthub.android.feature.subscription.SubscriptionScreen
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.common.Urls
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailViewModel
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsViewModel
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ChangePasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordViewModel
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerViewModel
import pl.cuyer.rusthub.presentation.features.settings.SettingsViewModel
import android.app.Activity
import pl.cuyer.rusthub.presentation.navigation.ChangePassword
import pl.cuyer.rusthub.presentation.navigation.ConfirmEmail
import pl.cuyer.rusthub.presentation.navigation.Credentials
import pl.cuyer.rusthub.presentation.navigation.DeleteAccount
import pl.cuyer.rusthub.presentation.navigation.Onboarding
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
fun NavigationRoot(startDestination: NavKey = Onboarding) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    ObserveAsEvents(flow = SnackbarController.events, snackbarHostState) { event ->
        scope.launch {
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
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>()

    LaunchedEffect(startDestination) {
        if (backStack.firstOrNull() != startDestination) {
            backStack.clear()
            backStack.add(startDestination)
        }
    }

    @Composable
    fun AppScaffold(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Scaffold(
            modifier = modifier
                .fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            content = { contentPadding ->
                NavDisplay(
                    modifier = Modifier
                        .padding(contentPadding)
                        .consumeWindowInsets(contentPadding),
                    backStack = backStack,
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
                                stateProvider = { state },
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
                                stateProvider = { state },
                                uiEvent = viewModel.uiEvent,
                                onAction = viewModel::onAction,
                                onNavigate = { dest ->
                                    if (dest is ServerList) backStack.clear()
                                    backStack.add(dest)
                                },
                                onNavigateUp = {
                                    backStack.removeLastOrNull()
                                }
                            )
                        }
                        entry<ServerList>(metadata = ListDetailSceneStrategy.listPane()) {
                            val viewModel = koinViewModel<ServerViewModel>()
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            val paging = viewModel.paging.collectAsLazyPagingItems()
                            ServerScreen(
                                stateProvider = { state },
                                uiEvent = viewModel.uiEvent,
                                onAction = viewModel::onAction,
                                pagedList = paging,
                                onNavigate = { dest ->
                                    backStack.add(dest)
                                }
                            )
                        }
                        entry<ServerDetails>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
                            val viewModel: ServerDetailsViewModel = koinViewModel(
                                key = key.id.toString()
                            ) { parametersOf(key.id, key.name) }
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            ServerDetailsScreen(
                                stateProvider = { state },
                                uiEvent = viewModel.uiEvent,
                                onAction = viewModel::onAction,
                                onNavigate = { dest -> backStack.add(dest) }
                            )
                        }
                        entry<Settings> {
                            val viewModel = koinViewModel<SettingsViewModel>()
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            SettingsScreen(
                                stateProvider = { state },
                                uiEvent = viewModel.uiEvent,
                                onAction = viewModel::onAction,
                                onNavigate = { dest ->
                                    if (dest is Onboarding) {
                                        backStack.clear()
                                    }
                                    backStack.add(dest)
                                }
                            )
                        }
                        entry<DeleteAccount> {
                            val viewModel = koinViewModel<DeleteAccountViewModel>()
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            DeleteAccountScreen(
                                onNavigateUp = { backStack.removeLastOrNull() },
                                onNavigate = { dest ->
                                    if (dest is Onboarding) backStack.clear()
                                    backStack.add(dest)
                                },
                                uiEvent = viewModel.uiEvent,
                                stateProvider = { state },
                                onAction = viewModel::onAction
                            )
                        }
                        entry<UpgradeAccount> {
                            val viewModel = koinViewModel<UpgradeViewModel>()
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            UpgradeAccountScreen(
                                onNavigateUp = { backStack.removeLastOrNull() },
                                uiEvent = viewModel.uiEvent,
                                stateProvider = { state },
                                onAction = viewModel::onAction
                            )
                        }
                        entry<ConfirmEmail> {
                            val viewModel = koinViewModel<ConfirmEmailViewModel>()
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            ConfirmEmailScreen(
                                uiEvent = viewModel.uiEvent,
                                stateProvider = { state },
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
                                stateProvider = { state },
                                onAction = viewModel::onAction
                            )
                        }
                        entry<ChangePassword> {
                            val viewModel = koinViewModel<ChangePasswordViewModel>()
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            ChangePasswordScreen(
                                onNavigateUp = { backStack.removeLastOrNull() },
                                uiEvent = viewModel.uiEvent,
                                stateProvider = { state },
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
                                title = SharedRes.strings.terms_conditions.getString(context),
                                onNavigateUp = { backStack.removeLastOrNull() }
                            )
                        }
                        entry<Subscription> {
                            SubscriptionScreen(
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

    val current = backStack.lastOrNull()
    val showNav = current is ServerList || current is ServerDetails || current is Settings


    if (showNav) {
        NavigationSuiteScaffold(
            modifier = Modifier
                .navigationBarsPadding(),
            navigationItems = {
                val context = LocalContext.current
                NavigationSuiteItem(
                    selected = current is ServerList || current is ServerDetails,
                    onClick = {
                        if (backStack.lastOrNull() !is ServerList) {
                            while (backStack.lastOrNull() !is ServerList && backStack.isNotEmpty()) {
                                backStack.removeLastOrNull()
                            }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = SharedRes.strings.servers.getString(context)
                        )
                    },
                    label = { Text(SharedRes.strings.servers.getString(context)) }
                )
                NavigationSuiteItem(
                    selected = current is Settings,
                    onClick = {
                        if (backStack.lastOrNull() !is Settings) {
                            backStack.add(Settings)
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = SharedRes.strings.settings.getString(context)
                        )
                    },
                    label = { Text(SharedRes.strings.settings.getString(context)) }
                )
            },
            content = {
                AppScaffold()
            }
        )
    } else {
        AppScaffold(modifier = Modifier.navigationBarsPadding())
    }
}