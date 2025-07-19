package pl.cuyer.rusthub.android.feature.subscription

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

private data class Benefit(
    @DrawableRes
    val image: Int,
    val title: StringResource,
    val desc: StringResource
)

private val benefits = listOf(
    Benefit(
        getImageByFileName("il_rusthub_pro").drawableResId,
        SharedRes.strings.full_access,
        SharedRes.strings.get_access_to_all_pro_features
    ),
    Benefit(
        getImageByFileName("il_unlimited_favourites").drawableResId,
        SharedRes.strings.unlimited_favourites,
        SharedRes.strings.save_as_many_servers_as_you_like
    ),
    Benefit(
        getImageByFileName("il_unlimited_notifications").drawableResId,
        SharedRes.strings.unlimited_notifications,
        SharedRes.strings.get_notified_about_all_your_servers
    ),
    Benefit(
        getImageByFileName("il_support_development").drawableResId,
        SharedRes.strings.support_development,
        SharedRes.strings.help_us_keep_improving_rust_hub
    )
)

private enum class Plan(val label: StringResource, val billed: StringResource) {
    MONTHLY(
        SharedRes.strings.monthly,
        billed = SharedRes.strings.billed_monthly,
    ),
    YEARLY(SharedRes.strings.yearly, billed = SharedRes.strings.billed_yearly),
    LIFETIME(SharedRes.strings.lifetime, billed = SharedRes.strings.pay_once)
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun SubscriptionScreen(
    onNavigateUp: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf(Plan.MONTHLY) }
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
                    IconButton(onClick = onNavigateUp) {
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
        if (isTabletMode) {
            SubscriptionScreenExpanded(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                pagerState = pagerState,
                selectedPlan = selectedPlan,
                onPlanSelect = { selectedPlan = it },
                onNavigateUp = onNavigateUp,
                onPrivacyPolicy = onPrivacyPolicy,
                onTerms = onTerms
            )
        } else {
            SubscriptionScreenCompact(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                pagerState = pagerState,
                selectedPlan = selectedPlan,
                onPlanSelect = { selectedPlan = it },
                onNavigateUp = onNavigateUp,
                onPrivacyPolicy = onPrivacyPolicy,
                onTerms = onTerms
            )
        }
    }
}

@Composable
private fun SubscriptionScreenCompact(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    selectedPlan: Plan,
    onPlanSelect: (Plan) -> Unit,
    onNavigateUp: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SubscriptionMainContent(pagerState, selectedPlan, onPlanSelect, onNavigateUp, onPrivacyPolicy, onTerms)
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
    selectedPlan: Plan,
    onPlanSelect: (Plan) -> Unit,
    onNavigateUp: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit
) {
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
            SubscriptionMainContent(pagerState, selectedPlan, onPlanSelect, onNavigateUp, onPrivacyPolicy, onTerms)
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
private fun SubscriptionMainContent(
    pagerState: PagerState,
    selectedPlan: Plan,
    onPlanSelect: (Plan) -> Unit,
    onNavigateUp: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit
) {
    HorizontalPager(state = pagerState) { page ->
        val benefit = benefits[page]
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Image(
                modifier = Modifier
                    .size(200.dp)
                    .semantics { hideFromAccessibility() },
                painter = painterResource(benefit.image),
                contentDescription = null
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
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            spacing.small,
            Alignment.CenterHorizontally
        )
    ) {
        Plan.entries.forEach { plan ->
            val isSelected = plan == selectedPlan

            ElevatedCard(
                onClick = { onPlanSelect(plan) },
                modifier = Modifier.then(
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
                        color = Color(0xFF917200)
                    )
                    Text(stringResource(plan.label), style = MaterialTheme.typography.titleMedium)
                    Text(
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize(),
                        text = stringResource(plan.billed),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Thin)
                    )
                }
            }
        }
    }
    AppButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {}) {
        Text(stringResource(SharedRes.strings.subscribe_to_plan, stringResource(selectedPlan.label)))
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
                    color = Color(0xFF917200),
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
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                LookaheadScope {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
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

                            Icon(
                                modifier = Modifier
                                    .rotate(rotation),
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = if (expanded) {
                                    stringResource(SharedRes.strings.hide_answer)
                                } else {
                                    stringResource(SharedRes.strings.show_answer)
                                }
                            )
                        }
                        AnimatedVisibility(expanded) {
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

    LaunchedEffect(pagerState.currentPage, cooldownKey) {
        delay(delayMillis)
        val nextPage = (pagerState.currentPage + 1) % carouselSize
        scope.launch { pagerState.animateScrollToPage(nextPage) }
    }
}

@Preview
@Composable
private fun SubscriptionScreenPreview() {
    RustHubTheme {
        SubscriptionScreen(
            onNavigateUp = {},
            onPrivacyPolicy = {},
            onTerms = {}
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
