package pl.cuyer.rusthub.android.feature.settings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import pl.cuyer.rusthub.android.designsystem.shimmer
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.wrapContentSize
import pl.cuyer.rusthub.android.designsystem.defaultFadeTransition
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.rememberCurrentLanguage
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.presentation.features.settings.SettingsAction
import pl.cuyer.rusthub.presentation.features.settings.SettingsState
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.util.StoreNavigator
import pl.cuyer.rusthub.android.util.composeUtil.OnLifecycleEvent
import androidx.lifecycle.Lifecycle

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun SettingsScreen(
    onNavigate: (NavKey) -> Unit,
    uiEvent: Flow<UiEvent>,
    state: State<SettingsState>,
    onAction: (SettingsAction) -> Unit
) {
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }
    OnLifecycleEvent { event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            onAction(SettingsAction.OnResume)
        }
    }
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
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        val icon = Icons.AutoMirrored.Default.Logout
                        Icon(
                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor),
                            imageVector = icon,
                            contentDescription = stringResource(SharedRes.strings.logout_button)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            if (isTabletMode) {
                SettingsScreenExpanded(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(spacing.medium),
                    username = state.value.username,
                    provider = state.value.provider,
                    subscribed = state.value.subscribed,
                    anonymousExpiration = state.value.anonymousExpiration,
                    plan = state.value.currentPlan,
                    planExpiration = state.value.subscriptionExpiration,
                    status = state.value.subscriptionStatus,
                    loading = state.value.isLoading,
                    isPrivacyOptionsRequired = state.value.isPrivacyOptionsRequired,
                    onAction = onAction,
                    onThemeClick = { showThemeSheet = true },
                    onLanguageClick = { showLanguageSheet = true }
                )
            } else {
                SettingsScreenCompact(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(spacing.medium),
                    username = state.value.username,
                    provider = state.value.provider,
                    subscribed = state.value.subscribed,
                    anonymousExpiration = state.value.anonymousExpiration,
                    plan = state.value.currentPlan,
                    planExpiration = state.value.subscriptionExpiration,
                    status = state.value.subscriptionStatus,
                    loading = state.value.isLoading,
                    isPrivacyOptionsRequired = state.value.isPrivacyOptionsRequired,
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
                    useSystemColors = state.value.useSystemColors,
                    onThemeChange = { onAction(SettingsAction.OnThemeChange(it)) },
                    onDynamicColorsChange = { onAction(SettingsAction.OnDynamicColorsChange(it)) },
                    onUseSystemColorsChange = { onAction(SettingsAction.OnUseSystemColorsChange(it)) },
                    onDismiss = { showThemeSheet = false }
                )
            }
            if (showLanguageSheet) {
                val currentLanguage by rememberCurrentLanguage()
                LanguageBottomSheet(
                    sheetState = languageSheetState,
                    current = currentLanguage,
                    onSelect = {
                        onAction(SettingsAction.OnLanguageChange(it))
                    },
                    onDismiss = { showLanguageSheet = false }
                )
            }
            if (state.value.isLoggingOut) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator()
                }
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
    anonymousExpiration: String?,
    plan: SubscriptionPlan?,
    planExpiration: String?,
    status: String?,
    loading: Boolean,
    isPrivacyOptionsRequired: Boolean,
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
        AccountSection(
            provider = provider,
            subscribed = subscribed,
            anonymousExpiration = anonymousExpiration,
            plan = plan,
            planExpiration = planExpiration,
            status = status,
            loading = loading,
            onAction = onAction
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
        OtherSection(onAction, isPrivacyOptionsRequired)
    }
}

@Composable
private fun SettingsScreenExpanded(
    modifier: Modifier = Modifier,
    username: String?,
    provider: AuthProvider?,
    subscribed: Boolean,
    anonymousExpiration: String?,
    plan: SubscriptionPlan?,
    planExpiration: String?,
    status: String?,
    loading: Boolean,
    isPrivacyOptionsRequired: Boolean,
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
            AccountSection(
                provider = provider,
                subscribed = subscribed,
                anonymousExpiration = anonymousExpiration,
                plan = plan,
                planExpiration = planExpiration,
                status = status,
                loading = loading,
                onAction = onAction
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            OtherSection(onAction, isPrivacyOptionsRequired)
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

    SettingsButton(
        text = stringResource(SharedRes.strings.theme),
        icon = Icons.Default.Palette,
        onClick = onThemeClick
    )

    SettingsButton(
        text = stringResource(SharedRes.strings.language),
        icon = Icons.Default.Language,
        onClick = onLanguageClick
    )

    SettingsButton(
        text = stringResource(SharedRes.strings.notifications),
        icon = Icons.Default.Notifications,
        contentDescription = stringResource(SharedRes.strings.notifications_toggle),
        onClick = { onAction(SettingsAction.OnNotificationsClick) }
    )
}


@Composable
private fun AccountSection(
    provider: AuthProvider?,
    subscribed: Boolean,
    anonymousExpiration: String?,
    plan: SubscriptionPlan?,
    planExpiration: String?,
    status: String?,
    loading: Boolean,
    onAction: (SettingsAction) -> Unit
) {
    Text(
        text = stringResource(SharedRes.strings.account),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = spacing.small)
    )

    val storeNavigator = koinInject<StoreNavigator>()

    AnimatedContent(
        targetState = loading,
        transitionSpec = { defaultFadeTransition() }
    ) { isLoading ->
        if (isLoading) {
            AccountSectionShimmer()
        } else if (subscribed) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xsmall)) {
                Text(
                    text = stringResource(
                        SharedRes.strings.you_are_subscribed,
                        stringResource(plan?.label ?: SharedRes.strings.pro)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                planExpiration?.let {
                    Text(
                        text = stringResource(SharedRes.strings.subscription_expiration, it),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                status?.let {
                    Text(
                        text = stringResource(SharedRes.strings.status) + ": " + it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (plan != SubscriptionPlan.LIFETIME) {
                    SettingsButton(
                        text = stringResource(SharedRes.strings.manage_subscription),
                        icon = Icons.Default.CreditCard,
                        onClick = {
                            storeNavigator.openSubscriptionManagement(SubscriptionPlan.SUBSCRIPTION_ID)
                        }
                    )
                }
            }
        }
    }

    if (provider !in listOf(AuthProvider.ANONYMOUS, AuthProvider.GOOGLE)) {
        SettingsButton(
            text = stringResource(SharedRes.strings.change_password),
            icon = Icons.Default.Lock,
            contentDescription = stringResource(SharedRes.strings.change_password_button),
            onClick = { onAction(SettingsAction.OnChangePasswordClick) }
        )
    }

    if (provider != AuthProvider.ANONYMOUS) {
        SettingsButton(
            text = stringResource(SharedRes.strings.subscription),
            icon = Icons.Default.CreditCard,
            contentDescription = stringResource(SharedRes.strings.subscription_button),
            onClick = { onAction(SettingsAction.OnSubscriptionClick) }
        )
    }

    if (provider == AuthProvider.ANONYMOUS) {
        anonymousExpiration?.let {
            Text(
                text = stringResource(SharedRes.strings.temporary_account_expiration, it),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = spacing.small)
            )
        }
        SettingsButton(
            text = stringResource(SharedRes.strings.upgrade_account),
            icon = Icons.Default.ArrowUpward,
            contentDescription = stringResource(SharedRes.strings.upgrade_account_button),
            onClick = { onAction(SettingsAction.OnUpgradeAccount) }
        )
    } else {
        SettingsButton(
            text = stringResource(SharedRes.strings.delete_account),
            icon = Icons.Default.Delete,
            contentDescription = stringResource(SharedRes.strings.delete_account_button),
            onClick = { onAction(SettingsAction.OnDeleteAccount) },
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun OtherSection(
    onAction: (SettingsAction) -> Unit,
    isPrivacyOptionsRequired: Boolean
) {
    val storeNavigator = koinInject<StoreNavigator>()
    Text(
        text = stringResource(SharedRes.strings.other),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = spacing.small)
    )

    if (isPrivacyOptionsRequired) {
        val activity = LocalContext.current as Activity
        SettingsButton(
            text = stringResource(SharedRes.strings.manage_privacy),
            icon = Icons.Default.PrivacyTip,
            contentDescription = stringResource(SharedRes.strings.manage_privacy_button),
            onClick = {
                onAction(SettingsAction.OnManagePrivacy(activity))
            }
        )
    }

    SettingsButton(
        text = stringResource(SharedRes.strings.privacy_policy),
        icon = Icons.Default.Description,
        contentDescription = stringResource(SharedRes.strings.privacy_policy_button),
        onClick = { onAction(SettingsAction.OnPrivacyPolicy) }
    )

    SettingsButton(
        text = stringResource(SharedRes.strings.terms_conditions),
        icon = Icons.Default.Gavel,
        contentDescription = stringResource(SharedRes.strings.terms_conditions_button),
        onClick = { onAction(SettingsAction.OnTerms) }
    )

    SettingsButton(
        text = stringResource(SharedRes.strings.rate_application),
        icon = Icons.Default.Star,
        contentDescription = stringResource(SharedRes.strings.rate_application_button),
        onClick = { storeNavigator.openStore() }
    )

    SettingsButton(
        text = stringResource(SharedRes.strings.about),
        icon = Icons.Default.Info,
        contentDescription = stringResource(SharedRes.strings.about_button),
        onClick = { onAction(SettingsAction.OnAbout) }
    )

}

@Composable
private fun SettingsButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String = text,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    AppTextButton(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = color
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(start = spacing.small),
                    color = color
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowRight,
                contentDescription = contentDescription,
                tint = color
            )
        }
    }
}

@Composable
private fun GreetingSection(username: String?) {
    AnimatedContent(
        targetState = username,
        transitionSpec = { defaultFadeTransition() }
    ) { name ->
        if (name != null) {
            Text(
                text = stringResource(SharedRes.strings.hello_username, name),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = spacing.medium)
            )
        }
    }
}

@Composable
private fun AccountSectionShimmer() {
    Column(verticalArrangement = Arrangement.spacedBy(spacing.xsmall)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer()
        )
    }
}

@Composable
private fun SettingsShimmer(isTablet: Boolean) {
    if (isTablet) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(spacing.large)
        ) {
            SettingsColumnShimmer(Modifier.weight(1f))
            SettingsColumnShimmer(Modifier.weight(1f))
        }
    } else {
        SettingsColumnShimmer(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium)
        )
    }
}

@Composable
private fun SettingsColumnShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(28.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer()
        )
        repeat(8) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
        }
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    val state = remember { mutableStateOf(SettingsState(isLoading = false)) }
    RustHubTheme {
        SettingsScreen(
            onNavigate = {},
            uiEvent = MutableStateFlow(UiEvent.Navigate(Onboarding)),
            state = state,
            onAction = {}
        )
    }
}

