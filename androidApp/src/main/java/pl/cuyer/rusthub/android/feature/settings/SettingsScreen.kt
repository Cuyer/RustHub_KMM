package pl.cuyer.rusthub.android.feature.settings

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumTouchTargetSize
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.rememberCurrentLanguage
import pl.cuyer.rusthub.android.util.composeUtil.setLanguage
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.presentation.features.settings.SettingsAction
import pl.cuyer.rusthub.presentation.features.settings.SettingsState
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.util.StoreNavigator
import pl.cuyer.rusthub.util.AppInfo
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import java.util.Locale

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
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

    val themeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val languageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showThemeSheet by rememberSaveable { mutableStateOf(false) }
    var showLanguageSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(SharedRes.strings.settings),
                    fontWeight = FontWeight.SemiBold
                ) },
                actions = {
                    IconButton(
                        onClick = { onAction(SettingsAction.OnLogout) },
                        modifier = Modifier.minimumTouchTargetSize()
                    ) {
                        val icon = Icons.AutoMirrored.Default.Logout
                        Icon(
                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor),
                            imageVector = icon,
                            contentDescription = stringResource(SharedRes.strings.logout_button)
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
                .padding(spacing.medium)
        ) {
            if (state.value.isLoading) {
                LoadingIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            if (isTabletMode) {
                SettingsScreenExpanded(
                    username = state.value.username,
                    provider = state.value.provider,
                    subscribed = state.value.subscribed,
                    expiration = state.value.anonymousExpiration,
                    onAction = onAction,
                    onThemeClick = { showThemeSheet = true },
                    onLanguageClick = { showLanguageSheet = true }
                )
            } else {
                SettingsScreenCompact(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()),
                    username = state.value.username,
                    provider = state.value.provider,
                    subscribed = state.value.subscribed,
                    expiration = state.value.anonymousExpiration,
                    onAction = onAction,
                    onThemeClick = { showThemeSheet = true },
                    onLanguageClick = { showLanguageSheet = true }
                )
            }
            if (showThemeSheet) {
                ThemeBottomSheet(
                    sheetState = themeSheetState,
                    current = state.value.theme,
                    dynamicColors = state.value.dynamicColors,
                    onThemeChange = { onAction(SettingsAction.OnThemeChange(it)) },
                    onDynamicColorsChange = { onAction(SettingsAction.OnDynamicColorsChange(it)) },
                    onDismiss = { showThemeSheet = false }
                )
            }
            if (showLanguageSheet) {
                val currentLanguage by rememberCurrentLanguage()
                LanguageBottomSheet(
                    sheetState = languageSheetState,
                    current = currentLanguage,
                    onSelect = {
                        setLanguage(it)
                    },
                    onDismiss = { showLanguageSheet = false }
                )
            }
        }
    }
}

@Composable
private fun SettingsScreenCompact(
    modifier: Modifier = Modifier,
    username: String?,
    provider: AuthProvider?,
    subscribed: Boolean,
    expiration: String?,
    onAction: (SettingsAction) -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        GreetingSection(username)
        PreferencesSection(onAction, onThemeClick, onLanguageClick)
        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
        AccountSection(provider, subscribed, expiration, onAction)
        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
        OtherSection(onAction)
    }
}

@Composable
private fun SettingsScreenExpanded(
    modifier: Modifier = Modifier,
    username: String?,
    provider: AuthProvider?,
    subscribed: Boolean,
    expiration: String?,
    onAction: (SettingsAction) -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit
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
            PreferencesSection(onAction, onThemeClick, onLanguageClick)
            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
            AccountSection(provider, subscribed, expiration, onAction)
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
    onAction: (SettingsAction) -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit
) {
    Text(
        text = stringResource(SharedRes.strings.preferences),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = spacing.small)
    )

    AppTextButton(onClick = onThemeClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(SharedRes.strings.theme))
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = stringResource(SharedRes.strings.theme)
            )
        }
    }

    AppTextButton(onClick = onLanguageClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(SharedRes.strings.language))
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = stringResource(SharedRes.strings.language)
            )
        }
    }

    AppTextButton(
        onClick = { onAction(SettingsAction.OnNotificationsClick) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(SharedRes.strings.notifications))
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = stringResource(SharedRes.strings.notifications_toggle)
            )
        }
    }
}


@Composable
private fun AccountSection(
    provider: AuthProvider?,
    subscribed: Boolean,
    expiration: String?,
    onAction: (SettingsAction) -> Unit
) {
    Text(
        text = stringResource(SharedRes.strings.account),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = spacing.small)
    )

    if (provider !in listOf(AuthProvider.ANONYMOUS, AuthProvider.GOOGLE)) {
        AppTextButton(
            onClick = { onAction(SettingsAction.OnChangePasswordClick) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(SharedRes.strings.change_password))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowRight,
                    contentDescription = stringResource(SharedRes.strings.change_password_button)
                )
            }
        }
    }

    if (!subscribed && provider != AuthProvider.ANONYMOUS) {
        AppTextButton(
            onClick = { onAction(SettingsAction.OnSubscriptionClick) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(SharedRes.strings.subscription))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowRight,
                    contentDescription = stringResource(SharedRes.strings.subscription_button)
                )
            }
        }
    }

    if (provider == AuthProvider.ANONYMOUS) {
        expiration?.let {
            Text(
                text = stringResource(SharedRes.strings.temporary_account_expiration, it),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = spacing.small)
            )
        }
        AppTextButton(
            onClick = { onAction(SettingsAction.OnUpgradeAccount) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(SharedRes.strings.upgrade_account))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowRight,
                    contentDescription = stringResource(SharedRes.strings.upgrade_account_button)
                )
            }
        }
    } else {
        AppTextButton(
            onClick = { onAction(SettingsAction.OnDeleteAccount) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(SharedRes.strings.delete_account), color = MaterialTheme.colorScheme.error)
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowRight,
                    contentDescription = stringResource(SharedRes.strings.delete_account_button),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable

private fun OtherSection(onAction: (SettingsAction) -> Unit) {
    val storeNavigator = koinInject<StoreNavigator>()
    Text(
        text = stringResource(SharedRes.strings.other),
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
            Text(stringResource(SharedRes.strings.privacy_policy))
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = stringResource(SharedRes.strings.privacy_policy_button)
            )
        }
    }

    AppTextButton(
        onClick = { storeNavigator.openStore() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(SharedRes.strings.rate_application))
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = stringResource(SharedRes.strings.rate_application_button)
            )
        }
    }

    Text(
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        text = "${stringResource(SharedRes.strings.app_version)}: ${AppInfo.versionName}",
        textAlign = TextAlign.Center
    )
}

@Composable
private fun GreetingSection(username: String?) {
    if (username != null) {
        Text(
            text = stringResource(SharedRes.strings.hello_username, username),
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
    RustHubTheme {
        SettingsScreen(
            onNavigate = {},
            uiEvent = MutableStateFlow(UiEvent.Navigate(Onboarding)),
            stateProvider = { androidx.compose.runtime.mutableStateOf(SettingsState()) },
            onAction = {}
        )
    }
}

