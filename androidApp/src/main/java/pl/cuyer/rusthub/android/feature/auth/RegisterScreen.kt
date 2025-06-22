package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.compose.foundation.Image
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
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.designsystem.SignProviderButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.RegisterAction
import pl.cuyer.rusthub.presentation.features.auth.RegisterState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RegisterScreen(
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<RegisterState>,
    onAction: (RegisterAction) -> Unit,
) {
    val state = stateProvider()
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }


    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isTabletMode) {
            RegisterScreenExpanded(
                onRegister = {
                    focusManager.clearFocus()
                    onAction(RegisterAction.OnRegister)
                },
                state = state.value,
                onAction = onAction
            )
        } else {
            RegisterScreenCompact(
                onRegister = {
                    focusManager.clearFocus()
                    onAction(RegisterAction.OnRegister)
                },
                state = state.value,
                onAction = onAction
            )
        }

        if (state.value.isLoading) {
            LoadingIndicator()
        }
    }
}

@Composable
private fun RegisterScreenCompact(
    state: RegisterState,
    onRegister: () -> Unit,
    onAction: (RegisterAction) -> Unit
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

        Text(text = "Create your RustHub account", style = MaterialTheme.typography.headlineMedium)

        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.username,
            labelText = "Username",
            placeholderText = "Enter your username",
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            onValueChange = { onAction(RegisterAction.OnUsernameChange(it)) },
            isError = state.usernameError != null,
            errorText = state.usernameError
        )

        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.email,
            labelText = "E-mail",
            placeholderText = "Enter your e-mail",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onValueChange = { onAction(RegisterAction.OnEmailChange(it)) },
            isError = state.emailError != null,
            errorText = state.emailError
        )

        AppSecureTextField(
            value = state.password,
            labelText = "Password",
            placeholderText = "Enter your password",
            onSubmit = onRegister,
            modifier = Modifier.fillMaxWidth(),
            imeAction = if (state.username.isNotBlank() && state.email.isNotBlank()) ImeAction.Send else ImeAction.Done,
            onValueChange = { onAction(RegisterAction.OnPasswordChange(it)) },
            isError = state.passwordError != null,
            errorText = state.passwordError
        )

        AppButton(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        SignProviderButton(
            image = getImageByFileName("ic_google").drawableResId,
            contentDescription = "Google logo",
            text = "Sign in with Google",
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        ) {

        }

        SignProviderButton(
            image = getImageByFileName("ic_apple").drawableResId,
            contentDescription = "Apple logo",
            text = "Sign in with Apple",
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            tint = if (isSystemInDarkTheme()) Color.Black else Color.White
        ) {

        }

        SignProviderButton(
            image = getImageByFileName("ic_facebook").drawableResId,
            contentDescription = "Facebook logo",
            text = "Sign in with Facebook",
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF1877F2),
            contentColor = Color.White
        ) {

        }

        SignProviderButton(
            image = getImageByFileName("ic_x").drawableResId,
            contentDescription = "X logo",
            text = "Sign in with X",
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            tint = if (isSystemInDarkTheme()) Color.Black else Color.White
        ) {

        }
    }
}

@Composable
private fun RegisterScreenExpanded(
    state: RegisterState,
    onRegister: () -> Unit,
    onAction: (RegisterAction) -> Unit
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
                text = "Create your RustHub account",
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
                labelText = "Username",
                placeholderText = "Enter your username",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                onValueChange = { onAction(RegisterAction.OnUsernameChange(it)) },
                isError = state.usernameError != null,
                errorText = state.usernameError
            )

            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                labelText = "E-mail",
                placeholderText = "Enter your e-mail",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onValueChange = { onAction(RegisterAction.OnEmailChange(it)) },
                isError = state.emailError != null,
                errorText = state.emailError
            )

            AppSecureTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                labelText = "Password",
                placeholderText = "Enter password",
                onSubmit = { },
                imeAction = if (state.username.isNotBlank() && state.email.isNotBlank()) ImeAction.Send else ImeAction.Done,
                onValueChange = { onAction(RegisterAction.OnPasswordChange(it)) },
                isError = state.passwordError != null,
                errorText = state.passwordError
            )

            AppButton(
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }

            SignProviderButton(
                image = getImageByFileName("ic_google").drawableResId,
                contentDescription = "Google logo",
                text = "Sign in with Google",
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            ) {

            }

            SignProviderButton(
                image = getImageByFileName("ic_apple").drawableResId,
                contentDescription = "Apple logo",
                text = "Sign in with Apple",
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                tint = if (isSystemInDarkTheme()) Color.Black else Color.White
            ) {

            }

            SignProviderButton(
                image = getImageByFileName("ic_facebook").drawableResId,
                contentDescription = "Facebook logo",
                text = "Sign in with Facebook",
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF1877F2),
                contentColor = Color.White
            ) {

            }

            SignProviderButton(
                image = getImageByFileName("ic_x").drawableResId,
                contentDescription = "X logo",
                text = "Sign in with X",
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                tint = if (isSystemInDarkTheme()) Color.Black else Color.White
            ) {

            }
        }
    }
}