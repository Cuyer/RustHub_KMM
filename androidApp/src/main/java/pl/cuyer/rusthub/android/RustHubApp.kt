package pl.cuyer.rusthub.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.ui.theme.RustHubTheme
import pl.cuyer.rusthub.navigation.Destination
import pl.cuyer.rusthub.navigation.NavOptionsBuilder
import pl.cuyer.rusthub.navigation.NavigationAction
import pl.cuyer.rusthub.navigation.Navigator
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController

@Composable
fun RustHubApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val navigator = koinInject<Navigator>()
    val snackbarController = SnackbarController
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentDestination = navController.currentBackStackEntryAsState()

    ObserveAsEvents(flow = navigator.navigationActions) { action ->
        when (action) {
            is NavigationAction.Navigate -> {
                val navOptions = NavOptionsBuilder().apply(action.navOptions).build()
                navController.navigate(action.destination, navOptions)
            }
            is NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

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

    RustHubTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                modifier = Modifier.navigationBarsPadding(),
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                content = { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Destination.HomeGraph
                        ) {
                            navigation<Destination.HomeGraph>(
                                startDestination = Destination.Home
                            ) {
                                composable<Destination.Home> {

                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
