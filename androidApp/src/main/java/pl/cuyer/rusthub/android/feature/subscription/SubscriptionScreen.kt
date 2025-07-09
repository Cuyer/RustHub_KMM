package pl.cuyer.rusthub.android.feature.subscription

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import android.app.Activity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName

private data class Benefit(
    @DrawableRes
    val image: Int,
    val title: String,
    val desc: String
)

private val benefits = listOf(
    Benefit(
        getImageByFileName("il_rusthub_pro").drawableResId,
        "Full Access",
        "Get access to all PRO features "
    ),
    Benefit(
        getImageByFileName("il_unlimited_favourites").drawableResId,
        "Unlimited favourites",
        "Save as many servers as you like."
    ),
    Benefit(
        getImageByFileName("il_unlimited_notifications").drawableResId,
        "Unlimited notifications",
        "Get notified about all your servers."
    ),
    Benefit(
        getImageByFileName("il_support_development").drawableResId,
        "Support development",
        "Help us keep improving Rust Hub."
    )
)

private enum class Plan(val label: String, val billed: String) {
    MONTHLY(
        "Monthly",
        billed = "Billed monthly"
    ),
    YEARLY("Yearly", billed = "Billed yearly"), LIFETIME("Lifetime", billed = "Pay once")
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
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate up")
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
                modifier = Modifier.size(200.dp),
                painter = painterResource(benefit.image),
                contentDescription = null
            )
            Text(benefit.title, style = MaterialTheme.typography.titleMedium)
            Text(
                benefit.desc,
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
        modifier = Modifier.fillMaxWidth(),
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
                    if (isSelected) Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CardDefaults.elevatedShape
                    ) else Modifier
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(spacing.medium)
                        .widthIn(min = 60.dp, max = 80.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "PRO",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFFDDA0D)
                    )
                    Text(plan.label, style = MaterialTheme.typography.titleMedium)
                    Text(
                        plan.billed,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Thin)
                    )
                }
            }
        }
    }
    AppButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {}) { Text("Subscribe to ${selectedPlan.label} plan") }
    AppTextButton(onClick = onNavigateUp) { Text("Not now") }
    Text(
        textAlign = TextAlign.Center,
        text = "Cancel your subscription at any time.\n\nSubscriptions will automatically renew unless canceled within 24-hours before the end of the current period. You can cancel anytime through your Google Play Store settings.\nRust Hub Pro Lifetime is a one time in-app purchase",
        style = MaterialTheme.typography.bodySmall
    )
    Row(horizontalArrangement = Arrangement.spacedBy(spacing.medium)) {
        AppTextButton(onClick = onPrivacyPolicy) { Text("Privacy policy") }
        AppTextButton(onClick = onTerms) { Text("Terms & conditions") }
    }
}


@Composable
private fun ComparisonSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Need to compare?", style = MaterialTheme.typography.titleMedium)

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
                    "Free",
                    modifier = Modifier.weight(0.25f),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    "PRO",
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
                Text("Add notifications", modifier = Modifier.weight(0.5f))
                Text("3 max", modifier = Modifier.weight(0.25f), textAlign = TextAlign.Center)
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(0.25f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Add to favourites", modifier = Modifier.weight(0.5f))
                Text("3 max", modifier = Modifier.weight(0.25f), textAlign = TextAlign.Center)
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
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
        "What does Rust Hub Pro include?" to "Rust Hub Pro includes unlimited favourite servers and unlimited notifications.",
        "Is Rust Hub Pro a one-time payment or will it renew automatically?" to "The Rust Hub Pro monthly and yearly plans are subscriptions that renew automatically at the end of your subscription term to avoid any interruption to your service. If you cancel your subscription, you will continue to have access to Rust Hub Pro until your subscription expires. The Rust Hub Pro Lifetime plan is a one time purchase.",
        "Can I cancel my subscription anytime?" to "Yes, you can cancel your Rust Hub Pro monthly or yearly subscription whenever you want! You will continue to have access to Rust Hub Pro until your subscription expires.",
        "Can I switch subscription plans?" to "Yes, absolutely. You are able to change subscription plans at any time through the Subscription tab in Rust Hub settings.",
        "What happens if i switch devices or platforms?" to "You can sign into your account across platforms with the same user name and password. The subscription is connected to your account, not the platform you are using. You can be signed into two different devices simultaneously and even two different platforms at the same time.",
        "How does Rust Hub Pro Lifetime plan work?" to "The Rust Hub Pro Lifetime plan  is a one time purchase. You will have access to Rust Hub Pro forever.\n\nIf you are already subscribed to a monthly or yearly plan and want to switch to a lifetime plan, make sure to cancel your monthly or yearly subscription after you purchase the lifetime plan to avoid any recurring charges."
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Text("Any questions?", style = MaterialTheme.typography.titleMedium)
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
                                contentDescription = null
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
