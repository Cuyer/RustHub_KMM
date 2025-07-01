package pl.cuyer.rusthub.android.feature.onboarding

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
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
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.designsystem.SignProviderButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.Theme
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
    calculateWindowSizeClass(context as Activity)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        OnboardingContent(onAction = onAction, state = state.value)
    }
}

private data class Feature(val icon: ImageVector, val title: String, val description: String)

@Composable
private fun OnboardingContent(onAction: (OnboardingAction) -> Unit, state: OnboardingState) {
    val features = listOf(
        Feature(Icons.Default.Search, "Find Servers", "Search and explore Rust servers by name, type, last wipe or more."),
        Feature(Icons.Default.ContentCopy, "Copy IPs", "Quickly copy server IP addresses to send them to your friends."),
        Feature(Icons.Default.Info, "View Details", "See server info like time of last wipe, map, ranking and more."),
        Feature(Icons.Default.FilterList, "Smart Filters", "Narrow your search using advanced filtering options."),
        Feature(Icons.Default.Notifications, "Notifications", "Receive notifications about map and full wipes."),
        Feature(Icons.Default.Favorite, "Favourites", "Add servers to your favourites to easily access them.")
    )

    val pagerState = rememberPagerState(pageCount = { features.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically)
    ) {
        HeaderSection()

        HorizontalPager(state = pagerState) { page ->
            val feature = features[page]
            FeatureItem(feature.icon, feature.title, feature.description)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spacing.xsmall)) {
            repeat(features.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (selected) 8.dp else 6.dp)
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = CircleShape
                        )
                )
            }
        }

        AuthSection(state, onAction)

        if (state.showOtherOptions) {
            ActionButtons(onAction, state.isLoading)
        }
    }
}

@Composable
private fun AuthSection(state: OnboardingState, onAction: (OnboardingAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Text(text = "Hello", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "Lets start with your email",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AppTextField(
            value = state.email,
            onValueChange = { onAction(OnboardingAction.OnEmailChange(it)) },
            labelText = "Email",
            placeholderText = "Enter your email",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
            isError = state.emailError != null,
            errorText = state.emailError,
            modifier = Modifier.fillMaxWidth()
        )
        AppButton(
            onClick = { onAction(OnboardingAction.OnContinueWithEmail) },
            isLoading = state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Continue with email") }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("or")
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        SignProviderButton(
            image = getImageByFileName("ic_google").drawableResId,
            contentDescription = "Google logo",
            text = "Continue with Google",
            modifier = Modifier.fillMaxWidth()
        ) { onAction(OnboardingAction.OnGoogleLogin) }

        AppTextButton(onClick = { onAction(OnboardingAction.OnShowOtherOptions) }) {
            Text("Other options")
        }
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
private fun ActionButtons(onAction: (OnboardingAction) -> Unit, isLoading: Boolean) {
    AppOutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onAction(OnboardingAction.OnContinueAsGuest) },
        isLoading = isLoading
    ) {
        Text("Continue as Guest")
    }
}

@Composable
private fun FeatureItem(icon: ImageVector, title: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.size(40.dp),
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

@Preview
@Composable
private fun OnboardingPrev() {
    RustHubTheme(theme = Theme.SYSTEM) {
        OnboardingScreen(
            onNavigate = {},
            stateProvider = { mutableStateOf(OnboardingState()) },
            onAction = {},
            uiEvent = MutableStateFlow(UiEvent.Navigate(object : NavKey {}))
        )
    }
}
