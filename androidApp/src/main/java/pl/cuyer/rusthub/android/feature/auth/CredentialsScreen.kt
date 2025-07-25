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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.designsystem.SignProviderButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsAction
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun CredentialsScreen(
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    state: State<CredentialsState>,
    onAction: (CredentialsAction) -> Unit,
    onNavigateUp: () -> Unit = {},
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val currentState = state.value

    ObserveAsEvents(uiEvent) { event ->
        when (event) {
            is UiEvent.Navigate -> onNavigate(event.destination)
            is UiEvent.NavigateUp -> onNavigateUp()
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(SharedRes.strings.account),
                    fontWeight = FontWeight.SemiBold
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = { onAction(CredentialsAction.OnNavigateUp) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(SharedRes.strings.navigate_up)
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
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .fillMaxSize()
                    .semantics { hideFromAccessibility() }
                    .animateBounds(this)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { focusManager.clearFocus() }
            ) {
                CredentialsContent(
                    email = currentState.email,
                    userExists = currentState.userExists,
                    provider = currentState.provider,
                    username = currentState.username,
                    password = currentState.password,
                    usernameError = currentState.usernameError,
                    passwordError = currentState.passwordError,
                    isLoading = currentState.isLoading,
                    googleLoading = currentState.googleLoading,
                    isExpanded = isTabletMode,
                    onAction = onAction,
                    focusManager = focusManager
                )
            }
        }
    }
}

@Composable
private fun CredentialsStaticContent(
    modifier: Modifier = Modifier,
    email: String,
    userExists: Boolean,
    provider: AuthProvider?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Icon(
            modifier = Modifier
                .size(64.dp),
            painter = painterResource(getImageByFileName("ic_padlock").drawableResId),
            contentDescription = stringResource(SharedRes.strings.padlock_icon),
        )
        Spacer(Modifier.height(spacing.small))
        Text(
            text = if (userExists) {
                stringResource(SharedRes.strings.welcome_back)
            } else {
                stringResource(SharedRes.strings.create_new_account)
            },
            style = MaterialTheme.typography.headlineLarge
        )
        if (userExists && provider != AuthProvider.GOOGLE) {
            Text(
                text = stringResource(SharedRes.strings.enter_your_password),
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Spacer(Modifier.height(spacing.small))
        Text(
            text = if (userExists && provider == AuthProvider.GOOGLE) {
                stringResource(SharedRes.strings.this_rust_hub_account_is_already_connected_with_social_account)
            } else if (userExists) {
                stringResource(SharedRes.strings.use_your_password_to_sign_in_to_an_existing_account)
            } else {
                stringResource(SharedRes.strings.protect_your_account_by_providing_a_strong_password)
            },
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = email, style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun rememberSyncedTextFieldState(
    value: String,
): TextFieldState {
    val state = rememberTextFieldState(value)
    LaunchedEffect(value) {
        if (state.text.toString() != value) {
            state.setTextAndPlaceCursorAtEnd(value)
        }
    }
    return state
}

@Composable
private fun CredentialsContent(
    email: String,
    userExists: Boolean,
    provider: AuthProvider?,
    username: String,
    password: String,
    usernameError: String?,
    passwordError: String?,
    isLoading: Boolean,
    googleLoading: Boolean,
    isExpanded: Boolean,
    onAction: (CredentialsAction) -> Unit,
    focusManager: FocusManager,
) {
    if (isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CredentialsStaticContent(
                modifier = Modifier.weight(1f),
                email = email,
                userExists = userExists,
                provider = provider,
            )
            CredentialsForm(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                userExists = userExists,
                provider = provider,
                username = username,
                password = password,
                usernameError = usernameError,
                passwordError = passwordError,
                isLoading = isLoading,
                googleLoading = googleLoading,
                onAction = onAction,
                focusManager = focusManager,
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            CredentialsStaticContent(
                email = email,
                userExists = userExists,
                provider = provider,
            )
            CredentialsForm(
                userExists = userExists,
                provider = provider,
                username = username,
                password = password,
                usernameError = usernameError,
                passwordError = passwordError,
                isLoading = isLoading,
                googleLoading = googleLoading,
                onAction = onAction,
                focusManager = focusManager,
            )
        }
    }
}


@Composable
private fun CredentialsForm(
    modifier: Modifier = Modifier,
    userExists: Boolean,
    provider: AuthProvider?,
    username: String,
    password: String,
    usernameError: String?,
    passwordError: String?,
    isLoading: Boolean,
    googleLoading: Boolean,
    onAction: (CredentialsAction) -> Unit,
    focusManager: FocusManager,
) {
    val usernameState = rememberSyncedTextFieldState(username)
    val passwordState = rememberSyncedTextFieldState(password)

    val latestAction = rememberUpdatedState(onAction)
    val submitAction = remember(focusManager) {
        {
            focusManager.clearFocus()
            latestAction.value(CredentialsAction.OnUsernameChange(usernameState.text.toString()))
            latestAction.value(CredentialsAction.OnPasswordChange(passwordState.text.toString()))
            latestAction.value(CredentialsAction.OnSubmit)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        val keyboardState = keyboardAsState()
        if (userExists && provider == AuthProvider.GOOGLE) {
            SignProviderButton(
                image = getImageByFileName("ic_google").drawableResId,
                contentDescription = stringResource(SharedRes.strings.google_logo),
                text = stringResource(SharedRes.strings.continue_with_google),
                modifier = Modifier.fillMaxWidth(),
                isLoading = googleLoading,
                backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            ) {
                focusManager.clearFocus()
                onAction(CredentialsAction.OnGoogleLogin)
            }
        } else {
            if (!userExists) {
                AppTextField(
                    requestFocus = true,
                    textFieldState = usernameState,
                    labelText = stringResource(SharedRes.strings.username),
                    placeholderText = stringResource(SharedRes.strings.enter_your_username),
                    isError = usernameError != null,
                    errorText = usernameError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    focusManager = focusManager,
                    keyboardState = keyboardState,
                )
            }
            if (provider != AuthProvider.GOOGLE) {
                AppSecureTextField(
                    requestFocus = userExists,
                    textFieldState = passwordState,
                    labelText = stringResource(SharedRes.strings.password),
                    placeholderText = stringResource(SharedRes.strings.enter_your_password),
                    onSubmit = submitAction,
                    isError = passwordError != null,
                    errorText = passwordError,
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = when (userExists) {
                        true -> if (passwordState.text.isNotBlank()) ImeAction.Send else ImeAction.Done
                        false -> if (usernameState.text.isNotBlank() && passwordState.text.isNotBlank()) ImeAction.Send else ImeAction.Done
                    },
                    focusManager = focusManager,
                    keyboardState = keyboardState,
                )
                if (userExists) {
                    AppTextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {
                            focusManager.clearFocus()
                            onAction(CredentialsAction.OnForgotPassword)
                        },
                    ) {
                        Text(stringResource(SharedRes.strings.forgot_password))
                    }
                }
            }
        }
        if (provider != AuthProvider.GOOGLE) {
            SubmitButton(
                usernameState = usernameState,
                passwordState = passwordState,
                userExists = userExists,
                isLoading = isLoading,
                onSubmit = submitAction,
            )
        }
    }
}

@Composable
private fun SubmitButton(
    usernameState: TextFieldState,
    passwordState: TextFieldState,
    userExists: Boolean,
    isLoading: Boolean,
    onSubmit: () -> Unit,
) {
    val buttonEnabled by remember {
        derivedStateOf {
            userExists && passwordState.text.isNotBlank() ||
                (!userExists && usernameState.text.isNotBlank() && passwordState.text.isNotBlank())
        }
    }

    AppButton(
        onClick = onSubmit,
        isLoading = isLoading,
        enabled = buttonEnabled,
        modifier = Modifier
            .imePadding()
            .fillMaxWidth(),
    ) {
        Text(stringResource(SharedRes.strings.continue_further))
    }
}
