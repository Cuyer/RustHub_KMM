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
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.android.navigation.BottomNavKey
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.navigation.Navigator
import pl.cuyer.rusthub.android.navigation.NavigationState
import pl.cuyer.rusthub.android.navigation.bottomNavItems
import pl.cuyer.rusthub.android.navigation.toEntries
import pl.cuyer.rusthub.android.rememberNavigationEntryProvider
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.user.UserEvent
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.presentation.navigation.About
import pl.cuyer.rusthub.presentation.navigation.ChangePassword
import pl.cuyer.rusthub.presentation.navigation.DeleteAccount
import pl.cuyer.rusthub.presentation.navigation.ItemDetails
import pl.cuyer.rusthub.presentation.navigation.ItemList
import pl.cuyer.rusthub.presentation.navigation.MonumentDetails
import pl.cuyer.rusthub.presentation.navigation.MonumentList
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.RaidForm
import pl.cuyer.rusthub.presentation.navigation.RaidScheduler
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.Settings
import pl.cuyer.rusthub.presentation.navigation.Subscription
import pl.cuyer.rusthub.presentation.navigation.UpgradeAccount
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun NavigationRoot(navigationState: NavigationState) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val navigator = remember(navigationState) { Navigator(navigationState) }

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

    ObserveAsEvents(flow = UserEventController.events, key1 = navigationState) { event ->
        if (event is UserEvent.LoggedOut) {
            navigationState.resetTo(Onboarding)
        }
    }

    LaunchedEffect(navigationState.currentKey) { snackbarHostState.currentSnackbarData?.dismiss() }

    val currentBottomNav = navigationState.currentTopLevelKey.toBottomNavKey()

    if (currentBottomNav != null) {
        NavigationSuiteScaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            navigationItems = { BottomBarItems(currentBottomNav) { navigator.navigate(it.root) } },
            content = {
                AppScaffold(
                    snackbarHostState = snackbarHostState,
                    navigationState = navigationState,
                    navigator = navigator,
                )
            }
        )
    } else {
        AppScaffold(
            snackbarHostState = snackbarHostState,
            navigationState = navigationState,
            navigator = navigator,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun AppScaffold(
    snackbarHostState: SnackbarHostState,
    navigationState: NavigationState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val entryProvider = rememberNavigationEntryProvider(navigator)
    val entries = navigationState.toEntries(entryProvider)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { contentPadding ->
            NavDisplay(
                entries = entries,
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
                onBack = { navigator.goBack() },
            )
        },
    )
}

@Composable
private fun BottomBarItems(
    current: BottomNavKey,
    onNavigate: (BottomNavKey) -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    fun safeNavigate(item: BottomNavKey) {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return
        onNavigate(item)
    }

    bottomNavItems.forEach { item ->
        NavigationSuiteItem(
            selected = current == item,
            onClick = { safeNavigate(item) },
            icon = {
                Icon(
                    imageVector = item.icon,
                    contentDescription = stringResource(item.label)
                )
            },
            label = { Text(stringResource(item.label)) }
        )
    }
}

private fun NavKey?.toBottomNavKey(): BottomNavKey? = when (this) {
    ServerList, is ServerDetails -> BottomNavKey.Servers
    ItemList, is ItemDetails -> BottomNavKey.Items
    MonumentList, is MonumentDetails -> BottomNavKey.Monuments
    RaidScheduler, is RaidForm -> BottomNavKey.Raids
    Settings, ChangePassword, DeleteAccount, UpgradeAccount, About, is Subscription ->
        BottomNavKey.Settings

    else -> null
}
