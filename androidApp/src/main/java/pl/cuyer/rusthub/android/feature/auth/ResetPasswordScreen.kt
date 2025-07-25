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
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordAction
import pl.cuyer.rusthub.presentation.features.auth.password.ResetPasswordState
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun ResetPasswordScreen(
    onNavigateUp: () -> Unit,
    uiEvent: Flow<UiEvent>,
    state: State<ResetPasswordState>,
    onAction: (ResetPasswordAction) -> Unit
) {
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.NavigateUp) onNavigateUp()
    }

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(SharedRes.strings.reset_password),
                    fontWeight = FontWeight.SemiBold
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(SharedRes.strings.navigate_up)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LookaheadScope {
            val currentState = state.value
            if (isTabletMode) {
                ResetPasswordScreenExpanded(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                        .fillMaxSize()
                        .padding(spacing.medium)
                        .animateBounds(this)
                        .clickable(interactionSource, null) { focusManager.clearFocus() },
                    email = currentState.email,
                    emailError = currentState.emailError,
                    isLoading = currentState.isLoading,
                    onAction = onAction,
                    focusManager = focusManager
                )
            } else {
                ResetPasswordScreenCompact(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(spacing.medium)
                        .animateBounds(this)
                        .clickable(interactionSource, null) { focusManager.clearFocus() },
                    email = currentState.email,
                    emailError = currentState.emailError,
                    isLoading = currentState.isLoading,
                    onAction = onAction,
                    focusManager = focusManager
                )
            }
        }
    }
}

@Composable
private fun ResetPasswordScreenCompact(
    modifier: Modifier = Modifier,
    email: String,
    emailError: String?,
    isLoading: Boolean,
    onAction: (ResetPasswordAction) -> Unit,
    focusManager: FocusManager
) {
    val emailState = rememberSyncedTextFieldState(email)
    val latestAction = rememberUpdatedState(onAction)
    val sendAction = remember(focusManager) {
        {
            focusManager.clearFocus()
            latestAction.value(ResetPasswordAction.OnEmailChange(emailState.text.toString()))
            latestAction.value(ResetPasswordAction.OnSend)
        }
    }
    val buttonEnabled by remember {
        derivedStateOf { emailState.text.isNotBlank() }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        ResetPasswordStaticContent()
        ResetPasswordField(
            emailState = emailState,
            emailError = emailError,
            onSubmit = sendAction,
            focusManager = focusManager
        )
        AppButton(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth(),
            enabled = buttonEnabled,
            isLoading = isLoading,
            onClick = sendAction
        ) { Text(stringResource(SharedRes.strings.send_email)) }
    }
}

@Composable
private fun ResetPasswordScreenExpanded(
    modifier: Modifier = Modifier,
    email: String,
    emailError: String?,
    isLoading: Boolean,
    onAction: (ResetPasswordAction) -> Unit,
    focusManager: FocusManager
) {
    val emailState = rememberSyncedTextFieldState(email)
    val latestAction = rememberUpdatedState(onAction)
    val sendAction = remember(focusManager) {
        {
            focusManager.clearFocus()
            latestAction.value(ResetPasswordAction.OnEmailChange(emailState.text.toString()))
            latestAction.value(ResetPasswordAction.OnSend)
        }
    }
    val buttonEnabled by remember {
        derivedStateOf { emailState.text.isNotBlank() }
    }
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
            ResetPasswordField(
                emailState = emailState,
                emailError = emailError,
                onSubmit = sendAction,
                focusManager = focusManager
            )
            AppButton(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
                enabled = buttonEnabled,
                isLoading = isLoading,
                onClick = sendAction
            ) { Text(stringResource(SharedRes.strings.send_email)) }
        }
    }
}

@Composable
private fun ResetPasswordStaticContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(getImageByFileName("ic_padlock").drawableResId),
            contentDescription = stringResource(SharedRes.strings.forgot_password_icon)
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = stringResource(SharedRes.strings.enter_your_e_mail_to_receive_password_reset_link),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ResetPasswordField(
    emailState: TextFieldState,
    emailError: String?,
    onSubmit: () -> Unit,
    focusManager: FocusManager
) {
    val keyboardState = keyboardAsState()
    AppTextField(
        requestFocus = true,
        textFieldState = emailState,
        labelText = stringResource(SharedRes.strings.e_mail),
        placeholderText = stringResource(SharedRes.strings.enter_your_e_mail),
        keyboardType = KeyboardType.Email,
        imeAction = if (emailState.text.isNotBlank()) ImeAction.Send else ImeAction.Done,
        onSubmit = onSubmit,
        isError = emailError != null,
        errorText = emailError,
        modifier = Modifier.fillMaxWidth(),
        focusManager = focusManager,
        keyboardState = keyboardState
    )
}

@Composable
private fun rememberSyncedTextFieldState(value: String): TextFieldState {
    val state = rememberTextFieldState(value)
    LaunchedEffect(value) {
        if (state.text.toString() != value) {
            state.setTextAndPlaceCursorAtEnd(value)
        }
    }
    return state
}
