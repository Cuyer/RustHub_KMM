package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordAction
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun ResetPasswordScreen(
    onNavigateUp: () -> Unit,
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<ResetPasswordState>,
    onAction: (ResetPasswordAction) -> Unit
) {
    val state = stateProvider()
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.NavigateUp) onNavigateUp()
    }

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LookaheadScope {
            if (isTabletMode) {
                ResetPasswordScreenExpanded(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(spacing.medium)
                        .animateBounds(this)
                        .clickable(interactionSource, null) { focusManager.clearFocus() },
                    state = state.value,
                    onAction = onAction
                )
            } else {
                ResetPasswordScreenCompact(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(spacing.medium)
                        .animateBounds(this)
                        .clickable(interactionSource, null) { focusManager.clearFocus() },
                    state = state.value,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun ResetPasswordScreenCompact(
    modifier: Modifier = Modifier,
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        val focusManager = LocalFocusManager.current
        ResetPasswordStaticContent()
        ResetPasswordField(
            email = state.email,
            emailError = state.emailError,
            onAction = onAction
        )
        AppButton(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth(),
            enabled = state.email.isNotBlank(),
            isLoading = state.isLoading,
            onClick = {
                focusManager.clearFocus()
                onAction(ResetPasswordAction.OnSend)
            }
        ) { Text(SharedRes.strings.send_email.getString(LocalContext.current)) }
    }
}

@Composable
private fun ResetPasswordScreenExpanded(
    modifier: Modifier = Modifier,
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ResetPasswordStaticContent(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            val focusManager = LocalFocusManager.current
            ResetPasswordField(
                email = state.email,
                emailError = state.emailError,
                onAction = onAction
            )
            AppButton(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
                isLoading = state.isLoading,
                onClick = {
                    focusManager.clearFocus()
                    onAction(ResetPasswordAction.OnSend)
                }
            ) { Text(SharedRes.strings.send_email.getString(LocalContext.current)) }
        }
    }
}

@Composable
private fun ResetPasswordStaticContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier.fillMaxWidth()) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(getImageByFileName("ic_padlock").drawableResId),
            contentDescription = SharedRes.strings.forgot_password_icon.getString(context)
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = SharedRes.strings.reset_password.getString(context),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = SharedRes.strings.enter_your_e_mail_to_receive_password_reset_link.getString(
                context
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ResetPasswordField(
    email: String,
    emailError: String?,
    onAction: (ResetPasswordAction) -> Unit
) {
    val context = LocalContext.current
    AppTextField(
        requestFocus = true,
        value = email,
        onValueChange = { onAction(ResetPasswordAction.OnEmailChange(it)) },
        labelText = SharedRes.strings.e_mail.getString(context),
        placeholderText = SharedRes.strings.enter_your_e_mail.getString(context),
        keyboardType = KeyboardType.Email,
        imeAction = if (email.isNotBlank()) ImeAction.Send else ImeAction.Done,
        onSubmit = { onAction(ResetPasswordAction.OnSend) },
        isError = emailError != null,
        errorText = emailError,
        modifier = Modifier.fillMaxWidth()
    )
}
