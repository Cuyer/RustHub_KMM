package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.runtime.remember
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.designsystem.SignProviderButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeAction
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeState
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun UpgradeAccountScreen(
    uiEvent: Flow<UiEvent>,
    state: State<UpgradeState>,
    onAction: (UpgradeAction) -> Unit,
    onNavigateUp: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val activity = LocalActivity.current as Activity
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val currentState = state.value

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.NavigateUp) onNavigateUp()
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(SharedRes.strings.upgrade_account),
                    fontWeight = FontWeight.SemiBold
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(UpgradeAction.OnNavigateUp) },
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
                .clickable(interactionSource, null) { focusManager.clearFocus() }
        ) {
            if (isTabletMode) {
                UpgradeScreenExpanded(
                    username = currentState.username,
                    usernameError = currentState.usernameError,
                    email = currentState.email,
                    emailError = currentState.emailError,
                    password = currentState.password,
                    passwordError = currentState.passwordError,
                    isLoading = currentState.isLoading,
                    googleLoading = currentState.googleLoading,
                    onAction = onAction
                )
            } else {
                UpgradeScreenCompact(
                    username = currentState.username,
                    usernameError = currentState.usernameError,
                    email = currentState.email,
                    emailError = currentState.emailError,
                    password = currentState.password,
                    passwordError = currentState.passwordError,
                    isLoading = currentState.isLoading,
                    googleLoading = currentState.googleLoading,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun UpgradeScreenCompact(
    username: String,
    usernameError: String?,
    email: String,
    emailError: String?,
    password: String,
    passwordError: String?,
    isLoading: Boolean,
    googleLoading: Boolean,
    onAction: (UpgradeAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val usernameState = rememberSyncedTextFieldState(username)
    val emailState = rememberSyncedTextFieldState(email)
    val passwordState = rememberSyncedTextFieldState(password)

    val latestAction = rememberUpdatedState(onAction)
    val submitAction = remember(focusManager) {
        {
            focusManager.clearFocus()
            latestAction.value(UpgradeAction.OnUsernameChange(usernameState.text.toString()))
            latestAction.value(UpgradeAction.OnEmailChange(emailState.text.toString()))
            latestAction.value(UpgradeAction.OnPasswordChange(passwordState.text.toString()))
            latestAction.value(UpgradeAction.OnSubmit)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        UpgradeStaticContent()
        UpgradeFields(
            usernameState = usernameState,
            usernameError = usernameError,
            emailState = emailState,
            emailError = emailError,
            passwordState = passwordState,
            passwordError = passwordError,
            focusManager = focusManager,
            onSubmit = submitAction,
        )
        SubmitButton(
            usernameState = usernameState,
            emailState = emailState,
            passwordState = passwordState,
            isLoading = isLoading,
            onSubmit = submitAction,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(stringResource(SharedRes.strings.or_str))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        SignProviderButton(
            image = getImageByFileName("ic_google").drawableResId,
            contentDescription = stringResource(SharedRes.strings.google_logo),
            text = stringResource(SharedRes.strings.upgrade_with_google),
            modifier = Modifier.fillMaxWidth(),
            isLoading = googleLoading,
            backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            onClick = {
                onAction(UpgradeAction.OnGoogleLogin)
            }
        )
    }
}

@Composable
private fun UpgradeScreenExpanded(
    username: String,
    usernameError: String?,
    email: String,
    emailError: String?,
    password: String,
    passwordError: String?,
    isLoading: Boolean,
    googleLoading: Boolean,
    onAction: (UpgradeAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val usernameState = rememberSyncedTextFieldState(username)
    val emailState = rememberSyncedTextFieldState(email)
    val passwordState = rememberSyncedTextFieldState(password)

    val latestAction = rememberUpdatedState(onAction)
    val submitAction = remember(focusManager) {
        {
            focusManager.clearFocus()
            latestAction.value(UpgradeAction.OnUsernameChange(usernameState.text.toString()))
            latestAction.value(UpgradeAction.OnEmailChange(emailState.text.toString()))
            latestAction.value(UpgradeAction.OnPasswordChange(passwordState.text.toString()))
            latestAction.value(UpgradeAction.OnSubmit)
        }
    }
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
        UpgradeFields(
            usernameState = usernameState,
            usernameError = usernameError,
            emailState = emailState,
            emailError = emailError,
            passwordState = passwordState,
            passwordError = passwordError,
            focusManager = focusManager,
            onSubmit = submitAction,
        )
        SubmitButton(
            usernameState = usernameState,
            emailState = emailState,
            passwordState = passwordState,
            isLoading = isLoading,
            onSubmit = submitAction,
        )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(stringResource(SharedRes.strings.or_str))
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            SignProviderButton(
                image = getImageByFileName("ic_google").drawableResId,
                contentDescription = stringResource(SharedRes.strings.google_logo),
                text = stringResource(SharedRes.strings.upgrade_with_google),
                modifier = Modifier.fillMaxWidth(),
                isLoading = googleLoading,
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
            contentDescription = stringResource(SharedRes.strings.padlock_icon)
        )
        Spacer(Modifier.size(spacing.small))
        Text(
            text = stringResource(SharedRes.strings.provide_credentials_or_connect_google_account_to_upgrade),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun UpgradeFields(
    usernameState: TextFieldState,
    usernameError: String?,
    emailState: TextFieldState,
    emailError: String?,
    passwordState: TextFieldState,
    passwordError: String?,
    focusManager: FocusManager,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        val keyboardState = keyboardAsState()
        AppTextField(
            requestFocus = true,
            textFieldState = usernameState,
            labelText = stringResource(SharedRes.strings.username),
            placeholderText = stringResource(SharedRes.strings.enter_username),
            isError = usernameError != null,
            errorText = usernameError,
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text,
            focusManager = focusManager,
            keyboardState = keyboardState,
        )
        AppTextField(
            textFieldState = emailState,
            labelText = stringResource(SharedRes.strings.e_mail),
            placeholderText = stringResource(SharedRes.strings.enter_your_e_mail),
            isError = emailError != null,
            errorText = emailError,
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email,
            focusManager = focusManager,
            keyboardState = keyboardState,
        )
        AppSecureTextField(
            textFieldState = passwordState,
            labelText = stringResource(SharedRes.strings.password),
            placeholderText = stringResource(SharedRes.strings.enter_password),
            onSubmit = onSubmit,
            isError = passwordError != null,
            errorText = passwordError,
            modifier = Modifier.fillMaxWidth(),
            imeAction = if (
                usernameState.text.isNotBlank() &&
                    emailState.text.isNotBlank() &&
                    passwordState.text.isNotBlank()
            ) ImeAction.Send else ImeAction.Done,
            focusManager = focusManager,
            keyboardState = keyboardState,
        )
    }
}

@Composable
private fun SubmitButton(
    usernameState: TextFieldState,
    emailState: TextFieldState,
    passwordState: TextFieldState,
    isLoading: Boolean,
    onSubmit: () -> Unit,
) {
    val buttonEnabled by remember {
        derivedStateOf {
            usernameState.text.isNotBlank() &&
                emailState.text.isNotBlank() &&
                passwordState.text.isNotBlank()
        }
    }

    AppButton(
        onClick = onSubmit,
        isLoading = isLoading,
        enabled = buttonEnabled,
        modifier = Modifier
            .fillMaxWidth(),
    ) { Text(stringResource(SharedRes.strings.upgrade)) }
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

