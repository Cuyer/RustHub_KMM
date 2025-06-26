package pl.cuyer.rusthub.android.feature.onboarding

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppOutlinedButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingAction
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class)
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

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)

    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isTabletMode) {
            OnboardingScreenExpanded(onAction, state.value.isLoading)
        } else {
            OnboardingScreenCompact(onAction, state.value.isLoading)
        }
    }
}

@Composable
private fun OnboardingScreenCompact(onAction: (OnboardingAction) -> Unit, isLoading: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically)
    ) {
        HeaderSection()
        FeatureList()
        ActionButtons(onAction, isLoading)
    }
}

@Composable
private fun OnboardingScreenExpanded(onAction: (OnboardingAction) -> Unit, isLoading: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(spacing.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            HeaderSectionExpanded()
            FeatureList()
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionButtons(onAction, isLoading)
        }
    }
}

@Composable
fun HeaderSectionExpanded() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome to RustHub",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Your gateway to the Rust server world",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Image(
            painter = painterResource(id = getImageByFileName("rusthub_logo").drawableResId),
            contentDescription = "Application logo"
        )
    }
}


@Composable
private fun HeaderSection() {
    Image(
        painter = painterResource(id = getImageByFileName("rusthub_logo").drawableResId),
        contentDescription = "Application logo"
    )

    Text(
        text = "Welcome to RustHub",
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center
    )

    Text(
        text = "Your gateway to the Rust server world",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun FeatureList() {
    FeatureItem(
        Icons.Default.Search,
        "Find Servers",
        "Search and explore Rust servers by name, type, last wipe or more."
    )
    FeatureItem(
        Icons.Default.ContentCopy,
        "Copy IPs",
        "Quickly copy server IP addresses to send them to your friends."
    )
    FeatureItem(
        Icons.Default.Info,
        "View Details",
        "See server info like time of last wipe, map, ranking and more."
    )
    FeatureItem(
        Icons.Default.FilterList,
        "Smart Filters",
        "Narrow your search using advanced filtering options."
    )
    FeatureItem(
        Icons.Default.Notifications,
        "Notifications",
        "Receive notifications about map and full wipes."
    )
    FeatureItem(
        Icons.Default.Favorite,
        "Favourites",
        "Add servers to your favourites to easily access them."
    )
}

@Composable
private fun ActionButtons(onAction: (OnboardingAction) -> Unit, isLoading: Boolean) {
    AppButton(
        onClick = { onAction(OnboardingAction.OnLoginClick) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Log In")
    }

    AppButton(
        onClick = { onAction(OnboardingAction.OnRegisterClick) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Register")
    }

    AppOutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onAction(OnboardingAction.OnContinueAsGuest) },
        isLoading = isLoading
    ) {
        Text(
            text = "Continue as Guest",
        )
    }
}


@Composable
private fun FeatureItem(icon: ImageVector, title: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(spacing.xmedium))

        Column {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview(device = "spec:parent=pixel_5,orientation=landscape")
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
