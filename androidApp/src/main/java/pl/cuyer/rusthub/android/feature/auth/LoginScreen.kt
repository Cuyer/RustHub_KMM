package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.designsystem.SignProviderButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.presentation.features.auth.login.LoginAction
import pl.cuyer.rusthub.presentation.features.auth.login.LoginState
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class)
fun LoginScreen(
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<LoginState>,
    onAction: (LoginAction) -> Unit
) {
    val state = stateProvider()

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    focusManager.clearFocus()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isTabletMode) {
            LoginScreenExpanded(
                state = state.value,
                onLogin = {
                    focusManager.clearFocus()
                    onAction(LoginAction.OnLogin)
                },
                onAction = onAction
            )
        } else {
            LoginScreenCompact(
                state = state.value,
                onLogin = {
                    focusManager.clearFocus()
                    onAction(LoginAction.OnLogin)
                },
                onAction = onAction
            )
        }
    }
}

@Composable
private fun LoginScreenCompact(
    state: LoginState,
    onLogin: () -> Unit,
    onAction: (LoginAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(id = getImageByFileName("rusthub_logo").drawableResId),
            contentDescription = "Application logo"
        )

        Text(text = "Log in to RustHub", style = MaterialTheme.typography.headlineMedium)

        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.username,
            labelText = "Username or E-mail",
            placeholderText = "Enter your username or e-mail",
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            onValueChange = { onAction(LoginAction.OnUsernameChange(it)) },
            isError = state.usernameError != null,
            errorText = state.usernameError
        )

        AppSecureTextField(
            value = state.password,
            labelText = "Password",
            placeholderText = "Enter your password",
            onSubmit = onLogin,
            modifier = Modifier.fillMaxWidth(),
            imeAction = if (state.username.isNotBlank()) ImeAction.Send else ImeAction.Done,
            onValueChange = { onAction(LoginAction.OnPasswordChange(it)) },
            isError = state.passwordError != null,
            errorText = state.passwordError
        )

        AppButton(
            isLoading = state.isLoading,
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        SignProviderButton(
            image = getImageByFileName("ic_google").drawableResId,
            contentDescription = "Google logo",
            text = "Sign in with Google",
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        ) { onAction(LoginAction.OnGoogleLogin) }

    }
}

@Composable
private fun LoginScreenExpanded(
    state: LoginState,
    onLogin: () -> Unit,
    onAction: (LoginAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(spacing.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = getImageByFileName("rusthub_logo").drawableResId),
                contentDescription = "RustHub Logo"
            )

            Text(
                text = "Log in to RustHub",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically)
        ) {
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.username,
                labelText = "Username or E-mail",
                placeholderText = "Enter your username or e-mail",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                onValueChange = { onAction(LoginAction.OnUsernameChange(it)) },
                isError = state.usernameError != null,
                errorText = state.usernameError
            )

            AppSecureTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                labelText = "Password",
                placeholderText = "Enter password",
                onSubmit = onLogin,
                imeAction = if (state.username.isNotBlank()) ImeAction.Send else ImeAction.Done,
                onValueChange = { onAction(LoginAction.OnPasswordChange(it)) },
                isError = state.passwordError != null,
                errorText = state.passwordError
            )

            AppButton(
                isLoading = state.isLoading,
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log In")
            }

            SignProviderButton(
                image = getImageByFileName("ic_google").drawableResId,
                contentDescription = "Google logo",
                text = "Sign in with Google",
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            ) { onAction(LoginAction.OnGoogleLogin) }
        }
    }
}

@Preview
@Composable
private fun LoginPrev() {
    RustHubTheme(theme = Theme.SYSTEM) {
        LoginScreen(
            onNavigate = {},
            uiEvent = MutableStateFlow(UiEvent.Navigate(ServerList)),
            stateProvider = { androidx.compose.runtime.mutableStateOf(LoginState()) },
            onAction = {}
        )
    }
}
