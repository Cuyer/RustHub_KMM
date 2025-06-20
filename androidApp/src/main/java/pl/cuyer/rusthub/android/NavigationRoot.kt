package pl.cuyer.rusthub.android

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.android.feature.server.ServerDetails
import pl.cuyer.rusthub.android.feature.server.ServerScreen
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.navigation.TwoPaneScene
import pl.cuyer.rusthub.android.navigation.TwoPaneSceneStrategy
import pl.cuyer.rusthub.presentation.features.ServerViewModel
import pl.cuyer.rusthub.presentation.navigation.Destination.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.Destination.ServerList
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationRoot() {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarController = SnackbarController
    val scope = rememberCoroutineScope()
    val backStack = remember { mutableStateListOf<Any>(ServerList) }
    val coroutineScope = rememberCoroutineScope()

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
                onBack = { count ->
                    repeat(count) {
                        if (backStack.isNotEmpty()) {
                            backStack.removeLastOrNull()
                        }
                    }
                },
                sceneStrategy = TwoPaneSceneStrategy<Any>(),
                entryProvider = entryProvider {
                    entry<ServerList>(
                        metadata = TwoPaneScene.twoPane()
                    ) {
                        val viewModel = koinInject<ServerViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle()
                        val paging = viewModel.paging.collectAsLazyPagingItems()

                        ServerScreen(
                            stateProvider = { state },
                            onAction = viewModel::onAction,
                            uiEvent = viewModel.uiEvent,
                            onNavigate = { destination ->
                                backStack.add(destination)
                            },
                            pagedList = paging
                        )
                    }
                    entry<ServerDetails>(
                        metadata = TwoPaneScene.twoPane()
                    ) {
                        ServerDetails()
                    }
                }
            )
        }
    )
}
