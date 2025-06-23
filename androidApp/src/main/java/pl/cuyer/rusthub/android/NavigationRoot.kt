package pl.cuyer.rusthub.android

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import pl.cuyer.rusthub.android.feature.auth.LoginScreen
import pl.cuyer.rusthub.android.feature.auth.RegisterScreen
import pl.cuyer.rusthub.android.feature.onboarding.OnboardingScreen
import pl.cuyer.rusthub.android.feature.server.ServerDetailsScreen
import pl.cuyer.rusthub.android.feature.server.ServerScreen
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.domain.usecase.LogoutUserUseCase
import pl.cuyer.rusthub.presentation.features.auth.LoginViewModel
import pl.cuyer.rusthub.presentation.features.auth.register.RegisterViewModel
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerDetailsViewModel
import pl.cuyer.rusthub.presentation.features.server.ServerViewModel
import pl.cuyer.rusthub.presentation.navigation.Login
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.Register
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.ServerList
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
    val logoutUseCase = koinInject<LogoutUserUseCase>()

    @Composable
    fun AppScaffold(modifier: Modifier = Modifier) {
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
                        entry<Login> {
                            val viewModel = koinViewModel<LoginViewModel>()
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            LoginScreen(
                                stateProvider = { state },
                                uiEvent = viewModel.uiEvent,
                                onAction = viewModel::onAction,
                                onNavigate = { dest ->
                                    backStack.clear()
                                    backStack.add(dest)
                                }
                            )
                        }
                        entry<Register> {
                            val viewModel = koinViewModel<RegisterViewModel>()
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            RegisterScreen(
                                stateProvider = { state },
                                uiEvent = viewModel.uiEvent,
                                onAction = viewModel::onAction,
                                onNavigate = { dest ->
                                    backStack.clear()
                                    backStack.add(dest)
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
                                    if (dest is ServerDetails && backStack.lastOrNull() is ServerDetails) {
                                        backStack[backStack.lastIndex] = dest
                                    } else backStack.add(dest)
                                }
                            )
                        }
                        entry<ServerDetails>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
                            val viewModel: ServerDetailsViewModel =
                                koinViewModel { parametersOf(key.id, key.name) }
                            val state = viewModel.state.collectAsStateWithLifecycle()
                            ServerDetailsScreen(
                                stateProvider = { state },
                                uiEvent = viewModel.uiEvent,
                                onAction = viewModel::onAction,
                                onNavigate = { dest -> backStack.add(dest) }
                            )
                        }
                    },
                    sceneStrategy = listDetailStrategy
                )
            }
        )
    }

    val current = backStack.lastOrNull()
    val showNav = current is ServerList || current is ServerDetails

    if (showNav) {
        NavigationSuiteScaffold(
            modifier = Modifier
                .navigationBarsPadding(),
            navigationItems = {
                NavigationSuiteItem(
                    selected = current is ServerList || current is ServerDetails,
                    onClick = {
                        if (backStack.lastOrNull() !is ServerList) {
                            while (backStack.lastOrNull() !is ServerList && backStack.isNotEmpty()) {
                                backStack.removeLastOrNull()
                            }
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Servers") },
                    label = { Text("Servers") }
                )
                NavigationSuiteItem(
                    selected = false,
                    onClick = {
                        scope.launch {
                            logoutUseCase()
                            backStack.clear()
                            backStack.add(Onboarding)
                        }
                    },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Log out"
                        )
                    },
                    label = { Text("Log out") }
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