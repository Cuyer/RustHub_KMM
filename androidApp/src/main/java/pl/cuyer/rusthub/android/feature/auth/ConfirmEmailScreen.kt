package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailAction
import pl.cuyer.rusthub.presentation.features.auth.confirm.ConfirmEmailState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
fun ConfirmEmailScreen(
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<ConfirmEmailState>,
    onAction: (ConfirmEmailAction) -> Unit,
    onNavigateUp: () -> Unit = {},
    onNavigate: (NavKey) -> Unit,
) {
    val state = stateProvider()
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    ObserveAsEvents(uiEvent) { event ->
        when (event) {
            is UiEvent.Navigate -> onNavigate(event.destination)
            is UiEvent.NavigateUp -> onNavigateUp()
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate up")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LookaheadScope {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .animateBounds(this)
                    .clickable(interactionSource, null) { focusManager.clearFocus() }
            ) {
                if (isTabletMode) {
                    ConfirmEmailScreenExpanded(state.value, onAction)
                } else {
                    ConfirmEmailScreenCompact(state.value, onAction)
                }
            }
        }
    }
}

@Composable
private fun ConfirmEmailScreenCompact(state: ConfirmEmailState, onAction: (ConfirmEmailAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConfirmEmailStaticContent(state.email)
        AppButton(
            onClick = { onAction(ConfirmEmailAction.OnConfirm) },
            isLoading = state.isLoading,
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
        ) { Text("Confirmed") }
        TextButton(onClick = { onAction(ConfirmEmailAction.OnResend) }) {
            Text("Resend email")
        }
    }
}

@Composable
private fun ConfirmEmailScreenExpanded(state: ConfirmEmailState, onAction: (ConfirmEmailAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConfirmEmailStaticContent(state.email, Modifier.weight(1f))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.size(spacing.medium))
            AppButton(
                onClick = { onAction(ConfirmEmailAction.OnConfirm) },
                isLoading = state.isLoading,
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
            ) { Text("Confirmed") }
            TextButton(onClick = { onAction(ConfirmEmailAction.OnResend) }) {
                Text("Resend email")
            }
        }
    }
}

@Composable
private fun ConfirmEmailStaticContent(email: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(getImageByFileName("ic_padlock").drawableResId),
            contentDescription = "Mail Icon",
        )
        Spacer(Modifier.size(spacing.small))
        Text(text = "Confirm your email", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.size(spacing.small))
        Text(text = "We have sent a confirmation link to $email. After confirming, press the button below.", style = MaterialTheme.typography.bodyMedium)
    }
}
