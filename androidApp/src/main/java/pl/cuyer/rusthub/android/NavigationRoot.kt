package pl.cuyer.rusthub.android

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.WindowAdaptiveInfoDefault
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
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
import pl.cuyer.rusthub.domain.usecase.LogoutUserUseCase

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun NavigationRoot() {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarController = SnackbarController
    val scope = rememberCoroutineScope()

    ObserveAsEvents(flow = snackbarController.events, snackbarHostState) { event ->
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
    val backStack = rememberNavBackStack(Onboarding)
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>()
    val logoutUseCase = koinInject<LogoutUserUseCase>()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            val current = backStack.lastOrNull()
            if (current is ServerList || current is ServerDetails) {
                NavigationSuiteItem(
                    selected = current is ServerList,
                    onClick = {
                        if (backStack.lastOrNull() !is ServerList) {
                            while (backStack.lastOrNull() !is ServerList && backStack.isNotEmpty()) {
                                backStack.removeLastOrNull()
                            }
                        }
                    },
                    icon = { androidx.compose.material3.Icon(Icons.Default.List, contentDescription = "Servers") },
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
                    icon = { androidx.compose.material3.Icon(Icons.Default.Logout, contentDescription = "Log out") },
                    label = { Text("Log out") }
                )
            }
        },
        layoutType = NavigationSuiteScaffoldDefaults.navigationSuiteType(WindowAdaptiveInfoDefault)
    ) {
        Scaffold(
            modifier = Modifier
                .navigationBarsPadding(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            content = { innerPadding ->
                NavDisplay(
                    entryDecorators = listOf(
                        rememberSceneSetupNavEntryDecorator(),
                        rememberSavedStateNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                backStack = backStack,
                onBack = { keysToRemove -> repeat(keysToRemove) { backStack.removeLastOrNull() } },
                sceneStrategy = listDetailStrategy,
                entryProvider = entryProvider {
                    entry<Onboarding> {
                        val viewModel = koinViewModel<OnboardingViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        OnboardingScreen(
                            stateProvider = { state },
                            onAction = viewModel::onAction,
                            uiEvent = viewModel.uiEvent,
                            onNavigate = { destination -> backStack.add(destination) }
                        )
                    }
                    entry<Login> {
                        val viewModel = koinViewModel<LoginViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()

                        LoginScreen(
                            onNavigate = { destination ->
                                with(backStack) {
                                    clear()
                                    add(destination)
                                }
                            },
                            stateProvider = { state },
                            uiEvent = viewModel.uiEvent,
                            onAction = viewModel::onAction
                        )
                    }
                    entry<Register> {
                        val viewModel = koinViewModel<RegisterViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()

                        RegisterScreen(
                            onNavigate = { destination ->
                                with(backStack) {
                                    clear()
                                    add(destination)
                                }
                            },
                            stateProvider = { state },
                            uiEvent = viewModel.uiEvent,
                            onAction = viewModel::onAction
                        )
                    }
                    entry<ServerList>(
                        metadata = ListDetailSceneStrategy.listPane()
                    ) {
                        val viewModel = koinViewModel<ServerViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        val paging = viewModel.paging.collectAsLazyPagingItems()

                        ServerScreen(
                            stateProvider = { state },
                            onAction = viewModel::onAction,
                            uiEvent = viewModel.uiEvent,
                            onNavigate = { destination ->
                                val last = backStack.lastOrNull()
                                if (destination is ServerDetails && last is ServerDetails) {
                                    if (last != destination) {
                                        backStack[backStack.lastIndex] = destination
                                    }
                                } else {
                                    backStack.add(destination)
                                }
                            },
                            pagedList = paging
                        )
                    }
                    entry<ServerDetails>(
                        metadata = ListDetailSceneStrategy.detailPane()
                    ) { key ->
                        val viewModel = koinViewModel<ServerDetailsViewModel>() {
                            parametersOf(
                                key.id,
                                key.name
                            )
                        }
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        ServerDetailsScreen(
                            stateProvider = { state },
                            onAction = viewModel::onAction,
                            uiEvent = viewModel.uiEvent,
                            onNavigate = { destination ->
                                backStack.add(destination)
                            }
                        )
                    }
                }
            )
        }
    )
}
