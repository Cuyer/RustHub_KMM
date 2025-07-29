package pl.cuyer.rusthub.android.feature.subscription

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import pl.cuyer.rusthub.android.designsystem.shimmer
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.Lifecycle
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.designsystem.PlanSelectorShimmer
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.android.util.composeUtil.OnLifecycleEvent
import pl.cuyer.rusthub.android.util.prefersReducedMotion
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.BillingProduct
import pl.cuyer.rusthub.presentation.features.subscription.SubscriptionAction
import pl.cuyer.rusthub.presentation.features.subscription.SubscriptionState
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan
import pl.cuyer.rusthub.presentation.navigation.UiEvent

@Immutable
private data class SubscriptionBenefit(
    @DrawableRes
    val image: Int,
    val title: StringResource,
    val desc: StringResource,
    val iconDesc: StringResource
)

private val benefits = listOf(
    SubscriptionBenefit(
        getImageByFileName("il_rusthub_pro").drawableResId,
        SharedRes.strings.full_access,
        SharedRes.strings.get_access_to_all_pro_features,
        SharedRes.strings.full_access_icon
    ),
    SubscriptionBenefit(
        getImageByFileName("il_unlimited_favourites").drawableResId,
        SharedRes.strings.unlimited_favourites,
        SharedRes.strings.save_as_many_servers_as_you_like,
        SharedRes.strings.unlimited_favourites_icon
    ),
    SubscriptionBenefit(
        getImageByFileName("il_unlimited_notifications").drawableResId,
        SharedRes.strings.unlimited_notifications,
        SharedRes.strings.get_notified_about_all_your_servers,
        SharedRes.strings.unlimited_notifications_icon
    ),
    SubscriptionBenefit(
        getImageByFileName("il_no_ads").drawableResId,
        SharedRes.strings.no_ads_title,
        SharedRes.strings.no_ads_desc,
        SharedRes.strings.no_ads_icon_desc
    ),
    SubscriptionBenefit(
        getImageByFileName("il_support_development").drawableResId,
        SharedRes.strings.support_development,
        SharedRes.strings.help_us_keep_improving_rust_hub,
        SharedRes.strings.support_development_icon
    )
)



@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalAnimationApi::class, ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun SubscriptionScreen(
    onNavigateUp: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
    state: State<SubscriptionState>,
    onAction: (SubscriptionAction) -> Unit,
    uiEvent: Flow<UiEvent>
) {
    var selectedPlan by remember { mutableStateOf(state.value.currentPlan ?: SubscriptionPlan.MONTHLY) }
    LaunchedEffect(state.value.currentPlan) {
        state.value.currentPlan?.let { selectedPlan = it }
    }
    OnLifecycleEvent { event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            onAction(SubscriptionAction.OnResume)
        }
    }
    val pagerState = rememberPagerState(pageCount = { benefits.size })
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val isTabletMode = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(SharedRes.strings.subscription),
                    fontWeight = FontWeight.SemiBold
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            tint = contentColorFor(TopAppBarDefaults.topAppBarColors().containerColor),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(SharedRes.strings.navigate_up)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ObserveAsEvents(uiEvent) { event ->
            if (event is UiEvent.NavigateUp) onNavigateUp()
        }
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            if (state.value.hasError) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    item {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "(×_×)",
                                    style = MaterialTheme.typography.headlineLarge,
                                    textAlign = TextAlign.Center,
                                    fontSize = 96.sp
                                )
                                Text(
                                    text = stringResource(SharedRes.strings.error_oops),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            } else {
                if (isTabletMode) {
                    SubscriptionScreenExpanded(
                        modifier = Modifier.fillMaxSize(),
                        pagerState = pagerState,
                        selectedPlan = { selectedPlan },
                        onPlanSelect = { selectedPlan = it },
                        onNavigateUp = onNavigateUp,
                        onPrivacyPolicy = onPrivacyPolicy,
                        onTerms = onTerms,
                        products = state.value.products,
                        isLoading = state.value.isLoading,
                        currentPlan = state.value.currentPlan,
                        onAction = onAction
                    )
                } else {
                    SubscriptionScreenCompact(
                        modifier = Modifier.fillMaxSize(),
                        pagerState = pagerState,
                        selectedPlan = { selectedPlan },
                        onPlanSelect = { selectedPlan = it },
                        onNavigateUp = onNavigateUp,
                        onPrivacyPolicy = onPrivacyPolicy,
                        onTerms = onTerms,
                        products = state.value.products,
                        isLoading = state.value.isLoading,
                        currentPlan = state.value.currentPlan,
                        onAction = onAction
                    )
                }
                if (state.value.isProcessing) {
                    SubscriptionShimmer(
                        isTablet = isTabletMode,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionScreenCompact(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    selectedPlan: () -> SubscriptionPlan,
    onPlanSelect: (SubscriptionPlan) -> Unit,
    onNavigateUp: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
    products: Map<SubscriptionPlan, BillingProduct>,
    isLoading: Boolean,
    currentPlan: SubscriptionPlan?,
    onAction: (SubscriptionAction) -> Unit
) {
    val activity = LocalActivity.current as Activity
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BenefitCarousel(pagerState = pagerState)
        if (isLoading) {
            PlanSelectorShimmer(Modifier.fillMaxWidth())
        } else {
            PlanSelector(
                selectedPlan = selectedPlan,
                onPlanSelect = onPlanSelect,
                products = products,
                currentPlan = currentPlan
            )
        }
        SubscribeActions(selectedPlan, onNavigateUp, onPrivacyPolicy, onTerms, currentPlan, isLoading) {
            onAction(SubscriptionAction.Subscribe(selectedPlan(), activity))
        }
        Spacer(modifier = Modifier.height(spacing.medium))
        ComparisonSection()
        Spacer(modifier = Modifier.height(spacing.medium))
        FaqSection()
    }
}

@Composable
private fun SubscriptionScreenExpanded(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    selectedPlan: () -> SubscriptionPlan,
    onPlanSelect: (SubscriptionPlan) -> Unit,
    onNavigateUp: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
    products: Map<SubscriptionPlan, BillingProduct>,
    isLoading: Boolean,
    currentPlan: SubscriptionPlan?,
    onAction: (SubscriptionAction) -> Unit
) {
    val activity = LocalActivity.current as Activity
    Row(
        modifier = modifier.padding(spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BenefitCarousel(pagerState = pagerState)
            if (isLoading) {
                PlanSelectorShimmer(Modifier.fillMaxWidth())
            } else {
                PlanSelector(
                    selectedPlan = selectedPlan,
                    onPlanSelect = onPlanSelect,
                    products = products,
                    currentPlan = currentPlan
                )
            }
            SubscribeActions(selectedPlan, onNavigateUp, onPrivacyPolicy, onTerms, currentPlan, isLoading) {
                onAction(SubscriptionAction.Subscribe(selectedPlan(), activity))
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComparisonSection()
            Spacer(modifier = Modifier.height(spacing.medium))
            FaqSection()
        }
    }
}

@Composable
private fun BenefitCarousel(pagerState: PagerState) {
    HorizontalPager(state = pagerState) { page ->
        val benefit = benefits[page]
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Image(
                modifier = Modifier.size(200.dp),
                painter = painterResource(benefit.image),
                contentDescription = stringResource(benefit.iconDesc)
            )
            Text(stringResource(benefit.title), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(benefit.desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    CarouselAutoPlayHandler(pagerState, benefits.size)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            spacing.xsmall,
            Alignment.CenterHorizontally
        )
    ) {
        repeat(benefits.size) { index ->
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

@Composable
private fun PlanSelector(
    selectedPlan: () -> SubscriptionPlan,
    onPlanSelect: (SubscriptionPlan) -> Unit,
    products: Map<SubscriptionPlan, BillingProduct>,
    currentPlan: SubscriptionPlan?
) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            spacing.small,
            Alignment.CenterHorizontally
        )
    ) {
        val lifetimeOwned = currentPlan == SubscriptionPlan.LIFETIME
        SubscriptionPlan.entries.forEach { plan ->
            val isSelected = plan == selectedPlan()
            val sd = if (isSelected) {
                stringResource(SharedRes.strings.plan_selected)
            } else {
                stringResource(SharedRes.strings.plan_not_selected)
            }

            ElevatedCard(
                onClick = { onPlanSelect(plan) },
                enabled = !lifetimeOwned && plan != currentPlan,
                modifier = Modifier
                    .semantics {
                        role = Role.RadioButton
                        stateDescription = sd
                    }
                    .then(
                        if (isSelected) Modifier
                            .fillMaxHeight()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CardDefaults.elevatedShape
                            ) else Modifier.fillMaxHeight()
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(spacing.medium)
                        .widthIn(min = 60.dp, max = 80.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(SharedRes.strings.pro),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFDDA0D)
                    )
                    Text(stringResource(plan.label), style = MaterialTheme.typography.titleMedium)
                    Text(
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize(),
                        text = products[plan]?.price ?: stringResource(plan.billed),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Thin)
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscribeActions(
    selectedPlan: () -> SubscriptionPlan,
    onNavigateUp: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit,
    currentPlan: SubscriptionPlan?,
    isLoading: Boolean,
    onSubscribe: () -> Unit
) {
    val plan = selectedPlan()
    val samePlan = plan == currentPlan
    val sameProduct = plan.productId == currentPlan?.productId && !samePlan
    val lifetimeOwned = currentPlan == SubscriptionPlan.LIFETIME
    val text = when {
        lifetimeOwned -> stringResource(SharedRes.strings.lifetime_plan_active)
        samePlan -> stringResource(SharedRes.strings.subscribed)
        sameProduct -> stringResource(SharedRes.strings.change_plan)
        else -> stringResource(SharedRes.strings.subscribe_to_plan, stringResource(plan.label))
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer()
        )
    } else {
        AppButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSubscribe,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            enabled = !samePlan && !lifetimeOwned
        ) { Text(text) }
    }
    AppTextButton(onClick = onNavigateUp) {
        Text(stringResource(SharedRes.strings.not_now))
    }
    Text(
        textAlign = TextAlign.Center,
        text = stringResource(SharedRes.strings.subscription_disclaimer),
        style = MaterialTheme.typography.bodySmall
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


@Composable
private fun ComparisonSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(SharedRes.strings.need_to_compare),
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("", modifier = Modifier.weight(0.5f))
                Text(
                    stringResource(SharedRes.strings.free),
                    modifier = Modifier.weight(0.25f),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    stringResource(SharedRes.strings.pro),
                    modifier = Modifier.weight(0.25f),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFFDDA0D),
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(SharedRes.strings.add_notifications), modifier = Modifier.weight(0.5f))
                Text(stringResource(SharedRes.strings.three_max), modifier = Modifier.weight(0.25f), textAlign = TextAlign.Center)
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(SharedRes.strings.yes),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(0.25f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(SharedRes.strings.add_to_favourites), modifier = Modifier.weight(0.5f))
                Text(stringResource(SharedRes.strings.three_max), modifier = Modifier.weight(0.25f), textAlign = TextAlign.Center)
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(SharedRes.strings.yes),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(0.25f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(SharedRes.strings.no_ads_title), modifier = Modifier.weight(0.5f))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(SharedRes.strings.no),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(0.25f)
                )
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(SharedRes.strings.yes),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(0.25f)
                )
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun FaqSection() {
    val faqs = listOf(
        stringResource(SharedRes.strings.faq_include_question) to stringResource(SharedRes.strings.faq_include_answer),
        stringResource(SharedRes.strings.faq_renew_question) to stringResource(SharedRes.strings.faq_renew_answer),
        stringResource(SharedRes.strings.faq_cancel_question) to stringResource(SharedRes.strings.faq_cancel_answer),
        stringResource(SharedRes.strings.faq_switch_plan_question) to stringResource(SharedRes.strings.faq_switch_plan_answer),
        stringResource(SharedRes.strings.faq_devices_question) to stringResource(SharedRes.strings.faq_devices_answer),
        stringResource(SharedRes.strings.faq_lifetime_question) to stringResource(SharedRes.strings.faq_lifetime_answer)
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Text(
            stringResource(SharedRes.strings.any_questions),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(spacing.medium))
        faqs.forEach { (q, a) ->
            var expanded by remember { mutableStateOf(false) }
            val rotation by animateFloatAsState(
                targetValue = if (expanded) 180f else 0f,
                label = "rotation"
            )
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors().copy(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                LookaheadScope {
                    val sd = if (expanded) {
                        stringResource(SharedRes.strings.expanded)
                    } else {
                        stringResource(SharedRes.strings.collapsed)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = expanded,
                                role = Role.Button,
                                onValueChange = { expanded = !expanded }
                            )
                            .semantics {
                                stateDescription = sd
                            }
                            .padding(spacing.medium)
                            .animateBounds(this@LookaheadScope),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.weight(0.9f),
                                text = q,
                                style = MaterialTheme.typography.titleSmall
                            )

                            // Decorative arrow icon
                            Icon(
                                modifier = Modifier
                                    .rotate(rotation)
                                    .semantics { hideFromAccessibility() },
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }
                        AnimatedVisibility(
                            visible = expanded,
                            enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)),
                            exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy))
                        ) {
                            Text(
                                a,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = spacing.small)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarouselAutoPlayHandler(pagerState: PagerState, carouselSize: Int, delayMillis: Long = 5000L) {
    val reduceMotion = prefersReducedMotion()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val interactions = remember(pagerState.interactionSource) {
        pagerState.interactionSource.interactions
            .flowWithLifecycle(lifecycleOwner.lifecycle, androidx.lifecycle.Lifecycle.State.STARTED)
    }
    val scope = rememberCoroutineScope()
    var cooldownKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(interactions) {
        interactions.collectLatest { interaction ->
            when (interaction) {
                is DragInteraction.Start,
                is DragInteraction.Stop,
                is DragInteraction.Cancel -> cooldownKey++
            }
        }
    }

    LaunchedEffect(pagerState.currentPage, cooldownKey, reduceMotion) {
        if (reduceMotion) return@LaunchedEffect
        delay(delayMillis)
        val nextPage = (pagerState.currentPage + 1) % carouselSize
        scope.launch { pagerState.animateScrollToPage(nextPage) }
    }
}

@Preview
@Composable
private fun SubscriptionScreenPreview() {
    val state = remember { mutableStateOf(SubscriptionState()) }
    RustHubTheme {
        SubscriptionScreen(
            onNavigateUp = {},
            onPrivacyPolicy = {},
            onTerms = {},
            state = state,
            onAction = {},
            uiEvent = MutableStateFlow(UiEvent.NavigateUp)
        )
    }
}

@Preview
@Composable
private fun FaqSectionPreview() {
    RustHubTheme {
        FaqSection()
    }
}

@Preview
@Composable
private fun ComparisonSectionPreview() {
    RustHubTheme {
        ComparisonSection()
    }}

@Composable
private fun SubscriptionShimmer(isTablet: Boolean, modifier: Modifier = Modifier) {
    if (isTablet) {
        SubscriptionShimmerExpanded(modifier)
    } else {
        SubscriptionShimmerCompact(modifier)
    }
}

@Composable
private fun SubscriptionShimmerCompact(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer()
        )
        PlanSelectorShimmer(Modifier.fillMaxWidth())
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmer()
        )
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
        }
    }
}

@Composable
private fun SubscriptionShimmerExpanded(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
            PlanSelectorShimmer(Modifier.fillMaxWidth())
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(5) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .shimmer()
                )
            }
        }
    }
}
