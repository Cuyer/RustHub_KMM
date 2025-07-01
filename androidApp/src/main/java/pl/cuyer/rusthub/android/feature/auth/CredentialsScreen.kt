package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsAction
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CredentialsScreen(
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<CredentialsState>,
    onAction: (CredentialsAction) -> Unit,
) {
    val state = stateProvider()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    calculateWindowSizeClass(context as Activity)

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { focusManager.clearFocus() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically)
        ) {
            if (state.value.email.isBlank()) {
                AppTextField(
                    value = state.value.email,
                    onValueChange = { onAction(CredentialsAction.OnEmailChange(it)) },
                    labelText = "Email",
                    placeholderText = "Enter your email",
                    isError = state.value.emailError != null,
                    errorText = state.value.emailError,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(text = state.value.email, style = MaterialTheme.typography.titleMedium)
            }
            if (!state.value.userExists) {
                AppTextField(
                    value = state.value.username,
                    onValueChange = { onAction(CredentialsAction.OnUsernameChange(it)) },
                    labelText = "Username",
                    placeholderText = "Enter your username",
                    isError = state.value.usernameError != null,
                    errorText = state.value.usernameError,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AppSecureTextField(
                value = state.value.password,
                onValueChange = { onAction(CredentialsAction.OnPasswordChange(it)) },
                labelText = "Password",
                placeholderText = "Enter your password",
                onSubmit = { onAction(CredentialsAction.OnSubmit) },
                isError = state.value.passwordError != null,
                errorText = state.value.passwordError,
                modifier = Modifier.fillMaxWidth()
            )
            AppButton(
                onClick = { onAction(CredentialsAction.OnSubmit) },
                isLoading = state.value.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Continue") }
        }
    }
}
