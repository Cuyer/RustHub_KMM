package pl.cuyer.rusthub.android.feature.onboarding

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation3.runtime.NavKey
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.android.util.prefersReducedMotion
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppOutlinedButton
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.designsystem.AppTextField
import pl.cuyer.rusthub.android.designsystem.SignProviderButton
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingAction
import pl.cuyer.rusthub.presentation.features.onboarding.OnboardingState
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun OnboardingScreen(
    onNavigate: (NavKey) -> Unit,
    state: State<OnboardingState>,
    onAction: (OnboardingAction) -> Unit,
    uiEvent: Flow<UiEvent>
) {
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }

    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isTabletMode) {
            OnboardingContentExpanded(
                onAction = onAction,
                email = { state.value.email },
                emailError = { state.value.emailError },
                isLoading = { state.value.isLoading },
                googleLoading = { state.value.googleLoading },
                continueAsGuestLoading = { state.value.continueAsGuestLoading }
            )
        } else {
            OnboardingContent(
                onAction = onAction,
                email = { state.value.email },
                emailError = { state.value.emailError },
                isLoading = { state.value.isLoading },
                googleLoading = { state.value.googleLoading },
                continueAsGuestLoading = { state.value.continueAsGuestLoading }
            )
        }
    }
}

@Immutable
private data class Feature(
    val icon: ImageVector,
    val title: StringResource,
    val description: StringResource
)

private val features = listOf(
    Feature(
        Icons.Default.Search,
        SharedRes.strings.find_servers,
        SharedRes.strings.search_and_explore_rust_servers_by_name_type_last_wipe_or_more
    ),
    Feature(
        Icons.Default.ContentCopy,
        SharedRes.strings.copy_ips,
        SharedRes.strings.quickly_copy_server_ip_addresses_to_send_them_to_your_friends
    ),
    Feature(
        Icons.Default.Info,
        SharedRes.strings.view_details,
        SharedRes.strings.see_server_info_like_time_of_last_wipe_map_ranking_and_more
    ),
    Feature(
        Icons.Default.FilterList,
        SharedRes.strings.smart_filters,
        SharedRes.strings.narrow_your_search_using_advanced_filtering_options
    ),
    Feature(
        Icons.Default.Notifications,
        SharedRes.strings.notifications,
        SharedRes.strings.receive_notifications_about_map_and_full_wipes
    ),
    Feature(
        Icons.Default.Favorite,
        SharedRes.strings.favourites,
        SharedRes.strings.add_servers_to_your_favourites_to_easily_access_them
    ),
    Feature(
        Icons.Default.Inventory,
        SharedRes.strings.items,
        SharedRes.strings.search_items_check_details_how_to_craft_drop_and_raid_costs
    )
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun OnboardingContent(
    onAction: (OnboardingAction) -> Unit,
    email: () -> String,
    emailError: () -> String?,
    isLoading: () -> Boolean,
    googleLoading: () -> Boolean,
    continueAsGuestLoading: () -> Boolean
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val pagerState = rememberPagerState(pageCount = { features.size })

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .semantics { hideFromAccessibility() }
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
        FeatureCarousel(pagerState = pagerState)
        Spacer(modifier = Modifier.height(spacing.medium))
        AuthSection(
            email = email,
            emailError = emailError,
            isLoading = isLoading,
            googleLoading = googleLoading,
            continueAsGuestLoading = continueAsGuestLoading,
            onAction = onAction
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun OnboardingContentExpanded(
    onAction: (OnboardingAction) -> Unit,
    email: () -> String,
    emailError: () -> String?,
    isLoading: () -> Boolean,
    googleLoading: () -> Boolean,
    continueAsGuestLoading: () -> Boolean
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val pagerState = rememberPagerState(pageCount = { features.size })

    Row(
        modifier = Modifier
            .semantics { hideFromAccessibility() }
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
            FeatureCarousel(pagerState = pagerState)
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
            AuthSection(
                email = email,
                emailError = emailError,
                isLoading = isLoading,
                googleLoading = googleLoading,
                onAction = onAction,
                continueAsGuestLoading = continueAsGuestLoading
            )
        }
    }
}

@Composable
private fun FeatureCarousel(pagerState: PagerState) {
    SubcomposeLayout { constraints ->
        val itemHeights = features.map { feature ->
            val placeable = subcompose("featureItem_${feature.hashCode()}") {
                FeatureItem(feature.icon, feature.title, feature.description)
            }.map { it.measure(constraints) }
            placeable.maxOf { it.height }
        }
        val maxHeight = itemHeights.maxOrNull() ?: 0

        val content = subcompose("carousel") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(LocalDensity.current) { maxHeight.toDp() })
                ) { page ->
                    val feature = features[page]
                    FeatureItem(feature.icon, feature.title, feature.description)
                }

                CarouselAutoPlayHandler(pagerState, features.size)

                Spacer(modifier = Modifier.height(spacing.xmedium))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.xsmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(features.size) { index ->
                        val cd = stringResource(
                            SharedRes.strings.page_indicator,
                            index + 1,
                            features.size
                        )
                        val selected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (selected) 8.dp else 6.dp)
                                .background(
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    shape = CircleShape
                                )
                                .semantics { contentDescription = cd }
                        )
                    }
                }
            }
        }.map { it.measure(constraints) }

        layout(content.maxOf { it.width }, content.maxOf { it.height }) {
            content.forEach { it.place(0, 0) }
        }
    }
}

@Composable
private fun AuthSection(
    email: () -> String,
    emailError: () -> String?,
    isLoading: () -> Boolean,
    googleLoading: () -> Boolean,
    continueAsGuestLoading: () -> Boolean,
    onAction: (OnboardingAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        val focusManager = LocalFocusManager.current
        EmailIntroText()

        EmailTextField(
            email = email,
            emailError = emailError,
            onAction = onAction,
            focusManager = focusManager
        )

        ContinueWithEmailButton(
            email = email,
            isLoading = isLoading,
            onAction = onAction
        )

        OrDivider()

        GoogleButton(
            isLoading = googleLoading,
            onClick = {
                focusManager.clearFocus()
                onAction(OnboardingAction.OnGoogleLogin)
            }
        )

        OtherOptionsToggle(
            continueAsGuestLoading = continueAsGuestLoading,
            onAction = onAction,
            onClick = {
                focusManager.clearFocus()
            }
        )

        PoliciesDisclaimer(
            onPrivacyPolicy = { onAction(OnboardingAction.OnPrivacyPolicy) },
            onTerms = { onAction(OnboardingAction.OnTerms) }
        )
    }
}

@Composable
private fun EmailIntroText() {
    Text(
        text = stringResource(SharedRes.strings.let_s_start_with_your_email),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}


@Composable
private fun EmailTextField(
    email: () -> String,
    emailError: () -> String?,
    onAction: (OnboardingAction) -> Unit,
    focusManager: FocusManager
) {
    val emailState = rememberTextFieldState(email())
    LaunchedEffect(email) { emailState.setTextAndPlaceCursorAtEnd(email()) }

    LaunchedEffect(emailState) {
        snapshotFlow { emailState.text }
            .collect { typed ->
                onAction(OnboardingAction.OnEmailChange(typed.toString()))
            }
    }

    AppTextField(
        textFieldState = emailState,
        labelText = stringResource(SharedRes.strings.e_mail),
        placeholderText = stringResource(SharedRes.strings.enter_your_e_mail),
        keyboardType = KeyboardType.Email,
        imeAction = if (emailState.text.isNotBlank()) ImeAction.Send else ImeAction.Done,
        isError = emailError() != null,
        errorText = emailError(),
        onSubmit = {
            onAction(OnboardingAction.OnContinueWithEmail)
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardState = keyboardAsState(),
        focusManager = focusManager
    )
}

@Composable
private fun ContinueWithEmailButton(
    email: () -> String,
    isLoading: () -> Boolean,
    onAction: (OnboardingAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    AppButton(
        onClick = {
            focusManager.clearFocus()
            onAction(OnboardingAction.OnContinueWithEmail)
        },
        isLoading = isLoading(),
        modifier = Modifier.fillMaxWidth(),
        enabled = email().isNotBlank()
    ) {
        Text(stringResource(SharedRes.strings.continue_with_e_mail))
    }
}

@Composable
private fun OrDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(stringResource(SharedRes.strings.or_str))
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun OtherOptionsToggle(
    onClick: () -> Unit,
    onAction: (OnboardingAction) -> Unit,
    continueAsGuestLoading: () -> Boolean
) {
    var showOtherOptions by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (showOtherOptions) 180f else 0f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTextButton(
            onClick = {
                onClick
                showOtherOptions = !showOtherOptions
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                Text(stringResource(SharedRes.strings.other_options))
                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(SharedRes.strings.arrow_down)
                )
            }
        }

        AnimatedVisibility(
            visible = showOtherOptions,
            enter = slideInVertically(
                animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
            ) + scaleIn(
                animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
            ),
            exit = slideOutVertically(
                animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
            ) + scaleOut(
                animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
            )
        ) {
            ActionButtons(
                onAction,
                continueAsGuestLoading
            )
        }
    }
}


@Composable
private fun GoogleButton(
    isLoading: () -> Boolean,
    onClick: () -> Unit
) {
    SignProviderButton(
        image = getImageByFileName("ic_google").drawableResId,
        contentDescription = stringResource(SharedRes.strings.google_logo),
        text = stringResource(SharedRes.strings.continue_with_google),
        modifier = Modifier.fillMaxWidth(),
        isLoading = isLoading(),
        backgroundColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
        contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        onClick = onClick
    )
}


@Composable
private fun HeaderSection() {
    Image(
        painter = painterResource(id = getImageByFileName("rusthub_logo").drawableResId),
        contentDescription = stringResource(SharedRes.strings.application_logo)
    )

    Text(
        text = stringResource(SharedRes.strings.welcome_to_rusthub),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center
    )

    Text(
        text = stringResource(SharedRes.strings.your_gateway_to_the_rust_server_world),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ActionButtons(
    onAction: (OnboardingAction) -> Unit,
    continueAsGuestLoading: () -> Boolean
) {
    AppOutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onAction(OnboardingAction.OnContinueAsGuest) },
        isLoading = continueAsGuestLoading()
    ) {
        Text(stringResource(SharedRes.strings.continue_as_guest))
    }
}

@Composable
private fun PoliciesDisclaimer(onPrivacyPolicy: () -> Unit, onTerms: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(SharedRes.strings.onboarding_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.medium)) {
            AppTextButton(onClick = onPrivacyPolicy) {
                Text(stringResource(SharedRes.strings.privacy_policy))
            }
            AppTextButton(onClick = onTerms) {
                Text(stringResource(SharedRes.strings.terms_conditions))
            }
        }
    }
}

@Composable
private fun FeatureItem(icon: ImageVector, title: StringResource, description: StringResource) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(title),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(spacing.xmedium))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(description),
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
    val reduceMotion = prefersReducedMotion()
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

    LaunchedEffect(pagerState.currentPage, cooldownKey, reduceMotion) {
        if (reduceMotion) return@LaunchedEffect
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
    val state = remember { mutableStateOf(OnboardingState()) }
    RustHubTheme {
        OnboardingScreen(
            onNavigate = {},
            state = state,
            onAction = {},
            uiEvent = MutableStateFlow(UiEvent.Navigate(object : NavKey {}))
        )
    }
}
