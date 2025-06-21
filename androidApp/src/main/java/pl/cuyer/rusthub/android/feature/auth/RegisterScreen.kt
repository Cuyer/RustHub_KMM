package pl.cuyer.rusthub.android.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.presentation.navigation.ServerList
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@Composable
fun RegisterScreen(
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    onBack: () -> Unit
) {
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(spacing.large))
        Button(onClick = { onNavigate(ServerList) }) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(spacing.medium))
        TextButton(onClick = onBack) { Text("Back") }
    }
}

@Preview
@Composable
private fun RegisterPrev() {
    RustHubTheme {
        RegisterScreen(
            onNavigate = {},
            uiEvent = MutableStateFlow(UiEvent.Navigate(ServerList)),
            onBack = {}
        )
    }
}
