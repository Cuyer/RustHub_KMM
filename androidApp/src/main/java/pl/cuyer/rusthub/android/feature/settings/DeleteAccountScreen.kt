package pl.cuyer.rusthub.android.feature.settings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import pl.cuyer.rusthub.domain.model.AuthProvider
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountAction
import pl.cuyer.rusthub.presentation.features.auth.delete.DeleteAccountState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
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

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

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
        if (isTabletMode) {
            DeleteAccountScreenExpanded(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(spacing.medium),
                state = state.value,
                onAction = onAction
            )
        } else {
            DeleteAccountScreenCompact(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(spacing.medium),
                state = state.value,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun DeleteAccountScreenCompact(
    modifier: Modifier = Modifier,
    state: DeleteAccountState,
    onAction: (DeleteAccountAction) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        DeleteAccountStaticContent()
        DeleteAccountFields(
            provider = state.provider,
            username = state.username,
            password = state.password,
            usernameError = state.usernameError,
            passwordError = state.passwordError,
            onAction = onAction
        )
        AppButton(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth(),
            isLoading = state.isLoading,
            onClick = { onAction(DeleteAccountAction.OnDelete) }
        ) { Text("Delete account") }
    }
}

@Composable
private fun DeleteAccountScreenExpanded(
    modifier: Modifier = Modifier,
    state: DeleteAccountState,
    onAction: (DeleteAccountAction) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeleteAccountStaticContent(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            DeleteAccountFields(
                provider = state.provider,
                username = state.username,
                password = state.password,
                usernameError = state.usernameError,
                passwordError = state.passwordError,
                onAction = onAction
            )
            AppButton(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
                isLoading = state.isLoading,
                onClick = { onAction(DeleteAccountAction.OnDelete) }
            ) { Text("Delete account") }
        }
    }
}

@Composable
private fun DeleteAccountStaticContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(getImageByFileName("ic_x").drawableResId),
            contentDescription = "Delete Icon"
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = "Delete account",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = "Deleting your account is irreversible. All your data will be removed.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DeleteAccountFields(
    provider: AuthProvider?,
    username: String,
    password: String,
    usernameError: String?,
    passwordError: String?,
    onAction: (DeleteAccountAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        if (provider != AuthProvider.GOOGLE) {
            AppTextField(
                value = username,
                onValueChange = { onAction(DeleteAccountAction.OnUsernameChange(it)) },
                labelText = "Username",
                placeholderText = "Enter your username",
                isError = usernameError != null,
                errorText = usernameError,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
            AppSecureTextField(
                value = password,
                onValueChange = { onAction(DeleteAccountAction.OnPasswordChange(it)) },
                labelText = "Password",
                placeholderText = "Enter your password",
                onSubmit = { onAction(DeleteAccountAction.OnDelete) },
                isError = passwordError != null,
                errorText = passwordError,
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Send
            )
        }
    }
}
