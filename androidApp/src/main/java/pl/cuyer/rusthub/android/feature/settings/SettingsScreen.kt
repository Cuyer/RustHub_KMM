package pl.cuyer.rusthub.android.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppExposedDropdownMenu
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.features.settings.SettingsAction
import pl.cuyer.rusthub.presentation.features.settings.SettingsState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@Composable
fun SettingsScreen(
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    stateProvider: () -> State<SettingsState>,
    onAction: (SettingsAction) -> Unit
) {
    val state = stateProvider()
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium)
    ) {
        Text(
            text = "General",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = spacing.small)
        )
        AppExposedDropdownMenu(
            label = "Theme",
            options = Theme.entries.map { it.displayName },
            selectedValue = Theme.entries.indexOf(state.value.theme),
            onSelectionChanged = { onAction(SettingsAction.OnThemeChange(Theme.entries[it])) }
        )
        AppExposedDropdownMenu(
            label = "Language",
            options = pl.cuyer.rusthub.domain.model.Language.entries.map { it.displayName },
            selectedValue = pl.cuyer.rusthub.domain.model.Language.entries.indexOf(state.value.language),
            onSelectionChanged = { onAction(SettingsAction.OnLanguageChange(pl.cuyer.rusthub.domain.model.Language.entries[it])) }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
        Text(
            text = "Account",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = spacing.small)
        )
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onAction(SettingsAction.OnLogout) },
        ) {
            Row {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Log out",
                    modifier = Modifier.padding(end = spacing.small)
                )
                Text("Log out")
            }
        }
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    RustHubTheme {
        SettingsScreen(
            onNavigate = {},
            uiEvent = MutableStateFlow(UiEvent.Navigate(Onboarding)),
            stateProvider = { androidx.compose.runtime.mutableStateOf(SettingsState()) },
            onAction = {}
        )
    }
}
