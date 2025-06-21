package pl.cuyer.rusthub.android.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.onboarding.OnboardingAction
import pl.cuyer.rusthub.presentation.onboarding.OnboardingState

@Composable
fun OnboardingScreen(
    onNavigate: (NavKey) -> Unit,
    stateProvider: () -> State<OnboardingState>,
    onAction: (OnboardingAction) -> Unit,
    uiEvent: Flow<UiEvent>
) {
    val state = stateProvider()

    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome to RustHub", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(spacing.large))
        Button(onClick = { onAction(OnboardingAction.OnLoginClick) }) {
            Text("Log In")
        }
        Spacer(modifier = Modifier.height(spacing.medium))
        Button(onClick = { onAction(OnboardingAction.OnRegisterClick) }) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(spacing.medium))
        TextButton(onClick = { onAction(OnboardingAction.OnContinueAsGuest) }) {
            Text("Continue as Guest")
        }
    }
}

@Preview
@Composable
private fun OnboardingPrev() {
    RustHubTheme {
        OnboardingScreen(
            onNavigate = {},
            stateProvider = { mutableStateOf(OnboardingState()) },
            onAction = {},
            uiEvent = MutableStateFlow(UiEvent.Navigate(object : NavKey {}))
        )
    }
}
