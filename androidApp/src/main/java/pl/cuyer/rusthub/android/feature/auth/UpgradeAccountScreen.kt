package pl.cuyer.rusthub.android.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeAction
import pl.cuyer.rusthub.presentation.features.auth.upgrade.UpgradeState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradeAccountScreen(
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<UpgradeState>,
    onAction: (UpgradeAction) -> Unit,
    onNavigateUp: () -> Unit = {},
) {
    val state = stateProvider()
    ObserveAsEvents(uiEvent) { event ->
        when (event) {
            is UiEvent.NavigateUp -> onNavigateUp()
            else -> Unit
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upgrade account") },
                navigationIcon = {
                    IconButton(onClick = { onAction(UpgradeAction.OnNavigateUp) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate up"
                        )
                    }
                },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            AppTextField(
                requestFocus = true,
                value = state.value.username,
                onValueChange = { onAction(UpgradeAction.OnUsernameChange(it)) },
                labelText = "Username",
                placeholderText = "Enter username",
                isError = state.value.usernameError != null,
                errorText = state.value.usernameError,
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
            AppSecureTextField(
                value = state.value.password,
                onValueChange = { onAction(UpgradeAction.OnPasswordChange(it)) },
                labelText = "Password",
                placeholderText = "Enter password",
                onSubmit = { onAction(UpgradeAction.OnSubmit) },
                isError = state.value.passwordError != null,
                errorText = state.value.passwordError,
                modifier = Modifier.fillMaxWidth(),
                imeAction = if (state.value.username.isNotBlank() && state.value.email.isNotBlank() && state.value.password.isNotBlank()) ImeAction.Send else ImeAction.Done,
            )
            AppButton(
                onClick = { onAction(UpgradeAction.OnSubmit) },
                isLoading = state.value.isLoading,
                enabled = state.value.username.isNotBlank() && state.value.password.isNotBlank() && state.value.email.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upgrade")
            }
        }
    }
}
