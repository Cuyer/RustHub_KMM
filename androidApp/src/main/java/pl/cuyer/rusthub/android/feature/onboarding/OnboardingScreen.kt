package pl.cuyer.rusthub.android.feature.onboarding

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isTabletMode) {
            OnboardingContentExpanded(onAction = onAction, state = state.value)
        } else {
            OnboardingContent(onAction = onAction, state = state.value)
        }
    }
}

private data class Feature(val icon: ImageVector, val title: String, val description: String)

private val features = listOf(
    Feature(
        Icons.Default.Search,
        "Find Servers",
        "Search and explore Rust servers by name, type, last wipe or more."
    ),
    Feature(
        Icons.Default.ContentCopy,
        "Copy IPs",
        "Quickly copy server IP addresses to send them to your friends."
    ),
    Feature(
        Icons.Default.Info,
        "View Details",
        "See server info like time of last wipe, map, ranking and more."
    ),
    Feature(
        Icons.Default.FilterList,
        "Smart Filters",
        "Narrow your search using advanced filtering options."
    ),
    Feature(
        Icons.Default.Notifications,
        "Notifications",
        "Receive notifications about map and full wipes."
    ),
    Feature(
        Icons.Default.Favorite,
        "Favourites",
        "Add servers to your favourites to easily access them."
    )
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun OnboardingContent(onAction: (OnboardingAction) -> Unit, state: OnboardingState) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    val pagerState = rememberPagerState(pageCount = { features.size })


    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { focusManager.clearFocus() }
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically)
    ) {
        HeaderSection()

        Spacer(modifier = Modifier.height(spacing.medium))

        HorizontalPager(state = pagerState) { page ->
            val feature = features[page]
            FeatureItem(feature.icon, feature.title, feature.description)
        }

        CarouselAutoPlayHandler(pagerState, features.size)

        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.xsmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

        Spacer(modifier = Modifier.height(spacing.medium))
        AuthSection(state, onAction)

        LookaheadScope {
            AnimatedVisibility(
                visible = state.showOtherOptions,
                enter = slideInVertically() + scaleIn(),
                exit = slideOutVertically() + scaleOut()
            ) {
                ActionButtons(
                    modifier = Modifier
                        .animateBounds(this@LookaheadScope),
                    onAction,
                    state.isLoading
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun OnboardingContentExpanded(
    onAction: (OnboardingAction) -> Unit,
    state: OnboardingState
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val pagerState = rememberPagerState(pageCount = { features.size })

    Row(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { focusManager.clearFocus() }
            .fillMaxSize()
            .padding(spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(spacing.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                spacing.medium,
                Alignment.CenterVertically
            )
        ) {
            HeaderSection()

            Spacer(modifier = Modifier.height(spacing.medium))

            HorizontalPager(state = pagerState) { page ->
                val feature = features[page]
                FeatureItem(feature.icon, feature.title, feature.description)
            }

            CarouselAutoPlayHandler(pagerState, features.size)

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.xsmall)) {
                repeat(features.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (selected) 8.dp else 6.dp)
                            .background(
                                color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                spacing.medium,
                Alignment.CenterVertically
            )
        ) {
            AuthSection(state, onAction)

            LookaheadScope {
                AnimatedVisibility(
                    visible = state.showOtherOptions,
                    enter = slideInVertically() + scaleIn(),
                    exit = slideOutVertically() + scaleOut()
                ) {
                    ActionButtons(
                        modifier = Modifier.animateBounds(this@LookaheadScope),
                        onAction = onAction,
                        isLoading = state.isLoading
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthSection(state: OnboardingState, onAction: (OnboardingAction) -> Unit) {
    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        Text(
            text = "Let's start with your email",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AppTextField(
            value = state.email,
            onValueChange = { onAction(OnboardingAction.OnEmailChange(it)) },
            labelText = "Email",
            placeholderText = "Enter your email",
            keyboardType = KeyboardType.Email,
            imeAction = if (state.email.isNotBlank()) ImeAction.Send else ImeAction.Done,
            isError = state.emailError != null,
            errorText = state.emailError,
            onSubmit = {
                onAction(OnboardingAction.OnContinueWithEmail)
            },
            modifier = Modifier.fillMaxWidth()
        )
        AppButton(
            onClick = { onAction(OnboardingAction.OnContinueWithEmail) },
            isLoading = state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.email.isNotBlank()
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
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        ) { onAction(OnboardingAction.OnGoogleLogin) }

        AppTextButton(
            onClick = { onAction(OnboardingAction.OnShowOtherOptions) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                val rotation by animateFloatAsState(if (state.showOtherOptions) 180f else 0f)

                Text("Other options")
                Icon(
                    modifier = Modifier
                        .rotate(rotation),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Arrow down"
                )
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Image(
        painter = painterResource(id = getImageByFileName("rusthub_logo").drawableResId),
        contentDescription = "Application logo"
    )
    Spacer(modifier = Modifier.height(spacing.small))
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
private fun ActionButtons(
    modifier: Modifier,
    onAction: (OnboardingAction) -> Unit,
    isLoading: Boolean
) {
    AppOutlinedButton(
        modifier = modifier.fillMaxWidth(),
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

@Composable
fun CarouselAutoPlayHandler(
    pagerState: PagerState,
    carouselSize: Int,
    delayMillis: Long = 5000L
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val interactions = remember(pagerState.interactionSource) {
        pagerState.interactionSource.interactions
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }
    val scope = rememberCoroutineScope()
    var cooldownKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(interactions) {
        interactions.collectLatest { interaction ->
            when (interaction) {
                is DragInteraction.Start,
                is DragInteraction.Stop,
                is DragInteraction.Cancel -> {
                    cooldownKey++
                }
            }
        }
    }

    LaunchedEffect(pagerState.currentPage, cooldownKey) {
        delay(delayMillis)
        val nextPage = (pagerState.currentPage + 1) % carouselSize
        scope.launch {
            pagerState.animateScrollToPage(nextPage)
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
