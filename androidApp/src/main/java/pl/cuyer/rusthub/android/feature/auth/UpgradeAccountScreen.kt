package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.designsystem.SignProviderButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeAction
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun UpgradeAccountScreen(
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<UpgradeState>,
    onAction: (UpgradeAction) -> Unit,
    onNavigateUp: () -> Unit = {},
) {
    val state = stateProvider()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.NavigateUp) onNavigateUp()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onAction(UpgradeAction.OnNavigateUp) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = SharedRes.strings.navigate_up.getString(context)
                        )
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
                    UpgradeScreenExpanded(state.value, onAction)
                } else {
                    UpgradeScreenCompact(state.value, onAction)
                }
            }
        }
    }
}

@Composable
private fun UpgradeScreenCompact(state: UpgradeState, onAction: (UpgradeAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        UpgradeStaticContent()
        UpgradeFields(state, onAction)
        AppButton(
            onClick = { onAction(UpgradeAction.OnSubmit) },
            isLoading = state.isLoading,
            enabled = state.username.isNotBlank() &&
                state.password.isNotBlank() &&
                state.email.isNotBlank(),
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
        ) { Text(SharedRes.strings.upgrade.getString(context)) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(SharedRes.strings.or_str.getString(context))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        SignProviderButton(
            image = getImageByFileName("ic_google").drawableResId,
            contentDescription = SharedRes.strings.google_logo.getString(context),
            text = SharedRes.strings.upgrade_with_google.getString(context),
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.googleLoading,
            backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        ) { onAction(UpgradeAction.OnGoogleLogin) }
    }
}

@Composable
private fun UpgradeScreenExpanded(state: UpgradeState, onAction: (UpgradeAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UpgradeStaticContent(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            UpgradeFields(state, onAction)
            AppButton(
                onClick = { onAction(UpgradeAction.OnSubmit) },
                isLoading = state.isLoading,
                enabled = state.username.isNotBlank() &&
                    state.password.isNotBlank() &&
                    state.email.isNotBlank(),
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
            ) { Text(SharedRes.strings.upgrade.getString(context)) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(SharedRes.strings.or_str.getString(context))
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            SignProviderButton(
                image = getImageByFileName("ic_google").drawableResId,
                contentDescription = SharedRes.strings.google_logo.getString(context),
                text = SharedRes.strings.upgrade_with_google.getString(context),
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.googleLoading,
                backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            ) { onAction(UpgradeAction.OnGoogleLogin) }
        }
    }
}

@Composable
private fun UpgradeStaticContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(getImageByFileName("ic_rocket").drawableResId),
            contentDescription = SharedRes.strings.padlock_icon.getString(LocalContext.current)
        )
        Spacer(Modifier.size(spacing.small))
        Text(
            text = SharedRes.strings.upgrade_your_account.getString(LocalContext.current),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.size(spacing.small))
        Text(
            text = SharedRes.strings.provide_credentials_or_connect_google_account_to_upgrade.getString(LocalContext.current),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun UpgradeFields(state: UpgradeState, onAction: (UpgradeAction) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        AppTextField(
            requestFocus = true,
            value = state.username,
            onValueChange = { onAction(UpgradeAction.OnUsernameChange(it)) },
            labelText = SharedRes.strings.username.getString(LocalContext.current),
            placeholderText = SharedRes.strings.enter_username.getString(LocalContext.current),
            isError = state.usernameError != null,
            errorText = state.usernameError,
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text
        )
        AppTextField(
            value = state.email,
            onValueChange = { onAction(UpgradeAction.OnEmailChange(it)) },
            labelText = SharedRes.strings.e_mail.getString(LocalContext.current),
            placeholderText = SharedRes.strings.enter_e_mail.getString(LocalContext.current),
            isError = state.emailError != null,
            errorText = state.emailError,
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email
        )
        AppSecureTextField(
            value = state.password,
            onValueChange = { onAction(UpgradeAction.OnPasswordChange(it)) },
            labelText = SharedRes.strings.password.getString(LocalContext.current),
            placeholderText = SharedRes.strings.enter_password.getString(LocalContext.current),
            onSubmit = { onAction(UpgradeAction.OnSubmit) },
            isError = state.passwordError != null,
            errorText = state.passwordError,
            modifier = Modifier.fillMaxWidth(),
            imeAction = if (
                state.username.isNotBlank() &&
                    state.email.isNotBlank() &&
                    state.password.isNotBlank()
            ) ImeAction.Send else ImeAction.Done
        )
    }
}
