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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    UpgradeScreenExpanded(
                        username = state.value.username,
                        usernameError = state.value.usernameError,
                        email = state.value.email,
                        emailError = state.value.emailError,
                        password = state.value.password,
                        passwordError = state.value.passwordError,
                        isLoading = state.value.isLoading,
                        googleLoading = state.value.googleLoading,
                        onAction = onAction
                    )
                } else {
                    UpgradeScreenCompact(
                        username = state.value.username,
                        usernameError = state.value.usernameError,
                        email = state.value.email,
                        emailError = state.value.emailError,
                        password = state.value.password,
                        passwordError = state.value.passwordError,
                        isLoading = state.value.isLoading,
                        googleLoading = state.value.googleLoading,
                        onAction = onAction
                    )
                }
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        UpgradeStaticContent()
        UpgradeFields(
            username = username,
            usernameError = usernameError,
            email = email,
            emailError = emailError,
            password = password,
            passwordError = passwordError,
            onAction = onAction
        )
        AppButton(
            onClick = { onAction(UpgradeAction.OnSubmit) },
            isLoading = isLoading,
            enabled = username.isNotBlank() &&
                password.isNotBlank() &&
                email.isNotBlank(),
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
        ) { Text(stringResource(SharedRes.strings.upgrade)) }

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
                username = username,
                usernameError = usernameError,
                email = email,
                emailError = emailError,
                password = password,
                passwordError = passwordError,
                onAction = onAction
            )
            AppButton(
                onClick = { onAction(UpgradeAction.OnSubmit) },
                isLoading = isLoading,
                enabled = username.isNotBlank() &&
                    password.isNotBlank() &&
                    email.isNotBlank(),
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
            ) { Text(stringResource(SharedRes.strings.upgrade)) }

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
    username: String,
    usernameError: String?,
    email: String,
    emailError: String?,
    password: String,
    passwordError: String?,
    onAction: (UpgradeAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        val usernameState = rememberTextFieldState(username)
        LaunchedEffect(username) { usernameState.setTextAndPlaceCursorAtEnd(username) }
        AppTextField(
            requestFocus = true,
            textFieldState = usernameState,
            labelText = stringResource(SharedRes.strings.username),
            placeholderText = stringResource(SharedRes.strings.enter_username),
            isError = usernameError != null,
            errorText = usernameError,
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text
        )
        val emailState = rememberTextFieldState(email)
        LaunchedEffect(email) { emailState.setTextAndPlaceCursorAtEnd(email) }
        AppTextField(
            textFieldState = emailState,
            labelText = stringResource(SharedRes.strings.e_mail),
            placeholderText = stringResource(SharedRes.strings.enter_your_e_mail),
            isError = emailError != null,
            errorText = emailError,
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email
        )
        val passwordState = rememberTextFieldState(password)
        LaunchedEffect(password) { passwordState.setTextAndPlaceCursorAtEnd(password) }
        AppSecureTextField(
            textFieldState = passwordState,
            labelText = stringResource(SharedRes.strings.password),
            placeholderText = stringResource(SharedRes.strings.enter_password),
            onSubmit = {
                onAction(UpgradeAction.OnUsernameChange(usernameState.text))
                onAction(UpgradeAction.OnEmailChange(emailState.text))
                onAction(UpgradeAction.OnPasswordChange(passwordState.text))
                onAction(UpgradeAction.OnSubmit)
            },
            isError = passwordError != null,
            errorText = passwordError,
            modifier = Modifier.fillMaxWidth(),
            imeAction = if (
                usernameState.text.isNotBlank() &&
                    emailState.text.isNotBlank() &&
                    passwordState.text.isNotBlank()
            ) ImeAction.Send else ImeAction.Done
        )
    }
}
