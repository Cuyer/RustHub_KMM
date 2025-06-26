package pl.cuyer.rusthub.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppExposedDropdownMenu
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.presentation.features.settings.SettingsAction
import pl.cuyer.rusthub.presentation.features.settings.SettingsState
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { onAction(SettingsAction.OnLogout) }) {
                        val icon = Icons.AutoMirrored.Default.Logout
                        Icon(icon, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
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
            Text(
                text = "Preferences",
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
                options = Language.entries.map { it.displayName },
                selectedValue = Language.entries.indexOf(state.value.language),
                onSelectionChanged = { onAction(SettingsAction.OnLanguageChange(Language.entries[it])) }
            )

            AppTextButton(
                onClick = {}
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notifications")
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowRight,
                        contentDescription = "Notifications toggle"
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
            Text(
                text = "Account",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = spacing.small)
            )

            AppTextButton(
                onClick = { }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Change password")
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowRight,
                        contentDescription = "Change password button"
                    )
                }
            }

            AppTextButton(
                onClick = { }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Subscription")
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowRight,
                        contentDescription = "Subscription button"
                    )
                }
            }

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.elevatedButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                onClick = { onAction(SettingsAction.OnLogout) },
            ) {
                Text(
                    text = "Delete account"
                )

            }

            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
            Text(
                text = "Other",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = spacing.small)
            )

            AppTextButton(
                onClick = { }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Privacy policy")
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowRight,
                        contentDescription = "Privacy policy button"
                    )
                }
            }

            AppTextButton(
                onClick = { }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rate application")
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowRight,
                        contentDescription = "Rate application button"
                    )
                }
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                text = "App version: 1.0",
                textAlign = TextAlign.Center
            )
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
