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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.designsystem.SignProviderButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
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
    stateProvider: () -> State<CredentialsState>,
    onAction: (CredentialsAction) -> Unit,
    onNavigateUp: () -> Unit = {},
) {
    val state = stateProvider()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

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
                    IconButton(onClick = { onAction(CredentialsAction.OnNavigateUp) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate up"
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
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { focusManager.clearFocus() }
            ) {
                if (isTabletMode) {
                    CredentialsScreenExpanded(
                        email = state.value.email,
                        userExists = state.value.userExists,
                        provider = state.value.provider,
                        googleLoading = state.value.googleLoading,
                        username = state.value.username,
                        password = state.value.password,
                        passwordError = state.value.passwordError,
                        usernameError = state.value.usernameError,
                        isLoading = state.value.isLoading,
                        onAction = onAction
                    )
                } else {
                    CredentialsScreenCompact(
                        email = state.value.email,
                        userExists = state.value.userExists,
                        provider = state.value.provider,
                        googleLoading = state.value.googleLoading,
                        username = state.value.username,
                        password = state.value.password,
                        passwordError = state.value.passwordError,
                        usernameError = state.value.usernameError,
                        isLoading = state.value.isLoading,
                        onAction = onAction
                    )
                }
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
            contentDescription = "Padlock Icon",
        )
        Spacer(Modifier.height(spacing.small))
        Text(
            text = if (userExists) "Welcome back!" else "Create new account",
            style = MaterialTheme.typography.headlineLarge
        )
        if (userExists && provider != AuthProvider.GOOGLE) {
            Text(text = "Enter your password", style = MaterialTheme.typography.headlineLarge)
        }
        Spacer(Modifier.height(spacing.small))
        Text(
            text = if (userExists && provider == AuthProvider.GOOGLE) {
                "This Rust Hub account is already connected with social account."
            } else if (userExists) {
                "Use your password to sign in to an existing account."
            } else {
                "Protect your account by providing a strong password."
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
private fun CredentialsScreenCompact(
    email: String,
    userExists: Boolean,
    provider: AuthProvider?,
    googleLoading: Boolean,
    username: String,
    password: String,
    passwordError: String? = null,
    usernameError: String? = null,
    isLoading: Boolean,
    onAction: (CredentialsAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        CredentialsStaticContent(
            email = email,
            userExists = userExists,
            provider = provider
        )
        CredentialsFields(
            userExists = userExists,
            provider = provider,
            googleLoading = googleLoading,
            username = username,
            password = password,
            passwordError = passwordError,
            usernameError = usernameError,
            onAction = onAction
        )
        if (provider != AuthProvider.GOOGLE) {
            AppButton(
                onClick = { onAction(CredentialsAction.OnSubmit) },
                isLoading = isLoading,
                enabled = when (userExists) {
                    true -> password.isNotBlank()
                    false -> username.isNotBlank() && password.isNotBlank()
                },
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
            ) { Text("Continue") }
        }
    }
}

@Composable
private fun CredentialsScreenExpanded(
    email: String,
    userExists: Boolean,
    provider: AuthProvider?,
    googleLoading: Boolean,
    username: String,
    password: String,
    passwordError: String? = null,
    usernameError: String? = null,
    isLoading: Boolean,
    onAction: (CredentialsAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CredentialsStaticContent(
            modifier = Modifier.weight(1f),
            email = email,
            userExists = userExists,
            provider = provider
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            CredentialsFields(
                userExists = userExists,
                provider = provider,
                googleLoading = googleLoading,
                username = username,
                password = password,
                passwordError = passwordError,
                usernameError = usernameError,
                onAction = onAction
            )
            if (provider != AuthProvider.GOOGLE) {
                AppButton(
                    onClick = { onAction(CredentialsAction.OnSubmit) },
                    isLoading = isLoading,
                    enabled = when (userExists) {
                        true -> password.isNotBlank()
                        false -> username.isNotBlank() && password.isNotBlank()
                    },
                    modifier = Modifier
                        .imePadding()
                        .fillMaxWidth()
                ) { Text("Continue") }
            }
        }
    }
}

@Composable
private fun CredentialsFields(
    userExists: Boolean,
    provider: AuthProvider?,
    googleLoading: Boolean,
    username: String,
    password: String,
    passwordError: String?,
    usernameError: String?,
    onAction: (CredentialsAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (userExists && provider == AuthProvider.GOOGLE) {
            SignProviderButton(
                image = getImageByFileName("ic_google").drawableResId,
                contentDescription = "Google logo",
                text = "Continue with Google",
                modifier = Modifier.fillMaxWidth(),
                isLoading = googleLoading,
                backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White
            ) {
                onAction(CredentialsAction.OnGoogleLogin)
            }
            return
        }

        if (!userExists) {
            AppTextField(
                requestFocus = true,
                value = username,
                onValueChange = { onAction(CredentialsAction.OnUsernameChange(it)) },
                labelText = "Username",
                placeholderText = "Enter your username",
                isError = usernameError != null,
                errorText = usernameError,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        }
        if (provider != AuthProvider.GOOGLE) {
            AppSecureTextField(
                requestFocus = userExists,
                value = password,
                onValueChange = { onAction(CredentialsAction.OnPasswordChange(it)) },
                labelText = "Password",
                placeholderText = "Enter your password",
                onSubmit = { onAction(CredentialsAction.OnSubmit) },
                isError = passwordError != null,
                errorText = passwordError,
                modifier = Modifier.fillMaxWidth(),
                imeAction = when (userExists) {
                    true -> if (password.isNotBlank()) ImeAction.Send else ImeAction.Done
                    false -> if (username.isNotBlank() && password.isNotBlank()) ImeAction.Send else ImeAction.Done
                }
            )
            if (userExists) {
                AppTextButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = { onAction(CredentialsAction.OnForgotPassword) }
                ) { Text(text = "Forgot password?") }
            }
        }
    }
}
