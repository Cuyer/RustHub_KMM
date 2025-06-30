package pl.cuyer.rusthub.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountAction
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@Composable
fun DeleteAccountScreen(
    onNavigateUp: () -> Unit,
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<DeleteAccountState>,
    onAction: (DeleteAccountAction) -> Unit
) {
    val state = stateProvider()
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delete account") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            AppTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.value.username,
                labelText = "Username or E-mail",
                placeholderText = "Enter your username or e-mail",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                onValueChange = { onAction(DeleteAccountAction.OnUsernameChange(it)) },
                isError = state.value.usernameError != null,
                errorText = state.value.usernameError
            )
            AppSecureTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.value.password,
                labelText = "Password",
                placeholderText = "Enter your password",
                onSubmit = { onAction(DeleteAccountAction.OnDelete) },
                imeAction = ImeAction.Send,
                onValueChange = { onAction(DeleteAccountAction.OnPasswordChange(it)) },
                isError = state.value.passwordError != null,
                errorText = state.value.passwordError
            )
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.value.isLoading,
                onClick = { onAction(DeleteAccountAction.OnDelete) }
            ) {
                Text("Delete account")
            }
        }
    }
}
