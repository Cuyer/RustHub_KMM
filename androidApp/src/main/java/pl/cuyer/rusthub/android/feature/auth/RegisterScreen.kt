package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.android.designsystem.SignProviderButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.RegisterAction
import pl.cuyer.rusthub.presentation.features.auth.RegisterState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun RegisterScreen(
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    onBack: () -> Unit,
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

    if (isTabletMode) {
        RegisterScreenExpanded(
            onBack = onBack,
            onRegister = { username, password ->
                onAction(
                    RegisterAction.OnRegister(
                        email = "",
                        password = password,
                        username = username
                    )
                )
            },
            username = state.value.username,
            password = state.value.password,
            onPasswordChange = { onAction(RegisterAction.OnUpdatePassword(it)) },
            onUsernameChange = { onAction(RegisterAction.OnUpdateUsername(it)) }
        )
    } else {
        RegisterScreenCompact(
            onBack = onBack,
            onRegister = { username, password ->
                onAction(
                    RegisterAction.OnRegister(
                        email = "",
                        password = password,
                        username = username
                    )
                )
            },
            username = state.value.username,
            password = state.value.password,
            onPasswordChange = { onAction(RegisterAction.OnUpdatePassword(it)) },
            onUsernameChange = { onAction(RegisterAction.OnUpdateUsername(it)) }
        )
    }
}

@Composable
private fun RegisterScreenCompact(
    username: String = "",
    password: String = "",
    email: String = "",
    onPasswordChange: (String) -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onBack: () -> Unit,
    onRegister: (username: String, password: String) -> Unit
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

        OutlinedTextField(
            value = username,
            onValueChange = { onUsernameChange(it) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { onEmailChange(it) },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { onPasswordChange(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onRegister(username, password) },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape
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

        TextButton(
            onClick = onBack,
            shape = RectangleShape
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                text = "Back"
            )
        }
    }
}

@Composable
private fun RegisterScreenExpanded(
    username: String = "",
    password: String = "",
    email: String = "",
    onPasswordChange: (String) -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onBack: () -> Unit,
    onRegister: (username: String, password: String) -> Unit
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
            OutlinedTextField(
                value = username,
                onValueChange = { onUsernameChange(it) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { onEmailChange(it) },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { onPasswordChange(it) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onRegister(username, password) },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape
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

            TextButton(
                onClick = onBack
            )
            {
                Text(
                    text = "Back",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}