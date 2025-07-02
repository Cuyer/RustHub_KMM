package pl.cuyer.rusthub.android.feature.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppSecureTextField
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.feature.onboarding.OnboardingScreen
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsAction
import pl.cuyer.rusthub.presentation.features.auth.credentials.CredentialsState
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingState
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CredentialsScreen(
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<CredentialsState>,
    onAction: (CredentialsAction) -> Unit,
    onNavigateUp: () -> Unit = {},
) {
    //TODO obsługa kont google gdy przejdą do credential screena
    val state = stateProvider()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    calculateWindowSizeClass(context as Activity)

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { focusManager.clearFocus() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.medium)
                    .align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                CredentialsStaticContent(
                    email = state.value.email,
                    userExists = state.value.userExists,
                )

                if (!state.value.userExists) {
                    AppTextField(
                        requestFocus = true,
                        value = state.value.username,
                        onValueChange = { onAction(CredentialsAction.OnUsernameChange(it)) },
                        labelText = "Username",
                        placeholderText = "Enter your username",
                        isError = state.value.usernameError != null,
                        errorText = state.value.usernameError,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                }
                AppSecureTextField(
                    requestFocus = state.value.userExists,
                    value = state.value.password,
                    onValueChange = { onAction(CredentialsAction.OnPasswordChange(it)) },
                    labelText = "Password",
                    placeholderText = "Enter your password",
                    onSubmit = { onAction(CredentialsAction.OnSubmit) },
                    isError = state.value.passwordError != null,
                    errorText = state.value.passwordError,
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = when (state.value.userExists) {
                        true -> if (state.value.password.isNotBlank()) ImeAction.Send else ImeAction.Done
                        false -> if (state.value.username.isNotBlank() && state.value.password.isNotBlank()) ImeAction.Send else ImeAction.Done
                    }
                )
                if (state.value.userExists) {
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.End),
                        onClick = {}
                    ) {
                        Text(
                            text = "Forgot password?"
                        )
                    }
                }
            }
            AppButton(
                onClick = { onAction(CredentialsAction.OnSubmit) },
                isLoading = state.value.isLoading,
                enabled = when (state.value.userExists) {
                    true -> state.value.password.isNotBlank()
                    false -> state.value.username.isNotBlank() && state.value.password.isNotBlank()
                },
                modifier = Modifier
                    .imePadding()
                    .padding(spacing.medium)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) { Text("Continue") }
        }
    }
}

@Composable
private fun CredentialsStaticContent(
    modifier: Modifier = Modifier,
    email: String,
    userExists: Boolean
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
        if (userExists) {
            Text(text = "Enter your password", style = MaterialTheme.typography.headlineLarge)
        }
        Spacer(Modifier.height(spacing.small))
        Text(
            text = if (userExists) "Use your password to sign in to an existing account." else "Protect your account by providing a strong password.",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = email, style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
