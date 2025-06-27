package pl.cuyer.rusthub.android.feature.settings

import android.app.Activity
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppExposedDropdownMenu
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.designsystem.SubscriptionDialog
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
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

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

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
        SubscriptionDialog(
            showDialog = state.value.showSubscriptionDialog,
            onConfirm = { onAction(SettingsAction.OnSubscribe) },
            onDismiss = { onAction(SettingsAction.OnDismissSubscriptionDialog) }
        )
        if (isTabletMode) {
            SettingsScreenExpanded(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(spacing.medium),
                theme = state.value.theme,
                language = state.value.language,
                username = state.value.username,
                onAction = onAction
            )
        } else {
            SettingsScreenCompact(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(spacing.medium),
                theme = state.value.theme,
                language = state.value.language,
                username = state.value.username,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun SettingsScreenCompact(
    modifier: Modifier = Modifier,
    username: String?,
    theme: Theme,
    language: Language,
    onAction: (SettingsAction) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        GreetingSection(username)
        PreferencesSection(theme, language, onAction)
        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
        AccountSection(onAction)
        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
        OtherSection(onAction)
    }
}

@Composable
private fun SettingsScreenExpanded(
    modifier: Modifier = Modifier,
    username: String?,
    theme: Theme,
    language: Language,
    onAction: (SettingsAction) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            GreetingSection(username)
            PreferencesSection(theme, language, onAction)
            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
            AccountSection(onAction)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            OtherSection(onAction)
        }
    }
}

@Composable
private fun PreferencesSection(
    theme: Theme,
    language: Language,
    onAction: (SettingsAction) -> Unit
) {
    Text(
        text = "Preferences",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = spacing.small)
    )
    AppExposedDropdownMenu(
        label = "Theme",
        options = Theme.entries.map { it.displayName },
        selectedValue = Theme.entries.indexOf(theme),
        onSelectionChanged = { onAction(SettingsAction.OnThemeChange(Theme.entries[it])) }
    )
    AppExposedDropdownMenu(
        label = "Language",
        options = Language.entries.map { it.displayName },
        selectedValue = Language.entries.indexOf(language),
        onSelectionChanged = { onAction(SettingsAction.OnLanguageChange(Language.entries[it])) }
    )
    AppTextButton(
        onClick = { onAction(SettingsAction.OnNotificationsClick) }
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
}

@Composable
private fun AccountSection(onAction: (SettingsAction) -> Unit) {
    Text(
        text = "Account",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = spacing.small)
    )

    AppTextButton(
        onClick = { onAction(SettingsAction.OnChangePasswordClick) }
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
        onClick = { onAction(SettingsAction.OnSubscriptionClick) }
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
}

@Composable
private fun OtherSection(onAction: (SettingsAction) -> Unit) {
    Text(
        text = "Other",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = spacing.small)
    )

    AppTextButton(
        onClick = { onAction(SettingsAction.OnPrivacyPolicy) }
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

@Composable
private fun GreetingSection(username: String?) {
    if (username != null) {
        Text(
            text = "Hello $username!",
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = spacing.medium)
        )
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    RustHubTheme(theme = Theme.SYSTEM) {
        SettingsScreen(
            onNavigate = {},
            uiEvent = MutableStateFlow(UiEvent.Navigate(Onboarding)),
            stateProvider = { androidx.compose.runtime.mutableStateOf(SettingsState()) },
            onAction = {}
        )
    }
}
