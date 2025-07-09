package pl.cuyer.rusthub.android.feature.subscription

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.ImageVector
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.android.theme.spacing

private data class Benefit(val icon: ImageVector, val title: String, val desc: String)

private val benefits = listOf(
    Benefit(Icons.Default.Star, "Unlimited favourites", "Save as many servers as you like."),
    Benefit(Icons.Default.Notifications, "Unlimited notifications", "Get notified about all your servers."),
    Benefit(Icons.Default.Check, "Support development", "Help us keep improving Rust Hub.")
)

private enum class Plan(val label: String) { MONTHLY("Monthly"), YEARLY("Yearly"), LIFETIME("Lifetime") }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SubscriptionScreen(
    onNavigateUp: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTerms: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf(Plan.MONTHLY) }
    val pagerState = rememberPagerState(pageCount = { benefits.size })
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rust Hub Pro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate up")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            HorizontalPager(state = pagerState) { page ->
                val benefit = benefits[page]
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(benefit.icon, contentDescription = null)
                    Spacer(Modifier.width(spacing.medium))
                    Column {
                        Text(benefit.title, style = MaterialTheme.typography.titleMedium)
                        Text(benefit.desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            CarouselAutoPlayHandler(pagerState, benefits.size)
            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                Plan.values().forEach { plan ->
                    ElevatedCard(
                        onClick = { selectedPlan = plan },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(spacing.medium)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(plan.label, style = MaterialTheme.typography.titleMedium)
                            if (selectedPlan == plan) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        }
                    }
                }
            }
            AppButton(modifier = Modifier.fillMaxWidth(), onClick = {}) { Text("Subscribe to ${selectedPlan.label}") }
            AppTextButton(onClick = onNavigateUp) { Text("Not now") }
            Text(
                text = "Cancel anytime. Subscription renews automatically unless canceled 24 hours before end of period. Manage or cancel through store settings. Rust Hub Pro Lifetime is a one time purchase.",
                style = MaterialTheme.typography.bodySmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.medium)) {
                AppTextButton(onClick = onPrivacyPolicy) { Text("Privacy policy") }
                AppTextButton(onClick = onTerms) { Text("Terms & conditions") }
            }
            ComparisonSection()
            FaqSection()
        }
    }
}

@Composable
private fun ComparisonSection() {
    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        Text("Free vs Pro", style = MaterialTheme.typography.titleMedium)
        FlowRow(maxItemsInEachRow = 2, horizontalArrangement = Arrangement.spacedBy(spacing.large)) {
            Column {
                Text("Free", style = MaterialTheme.typography.titleSmall)
                Text("Up to 3 server notifications")
                Text("Up to 3 favourite servers")
            }
            Column {
                Text("Pro", style = MaterialTheme.typography.titleSmall)
                Text("Unlimited notifications")
                Text("Unlimited favourites")
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FaqSection() {
    val faqs = listOf(
        "What does Rust Hub Pro include?" to "Unlimited favourites and notifications.",
        "Is Rust Hub Pro a one-time payment or will it renew automatically?" to "Monthly and yearly plans renew automatically.",
        "Can I cancel my subscription anytime?" to "Yes, you can cancel anytime in store settings.",
        "Can I switch subscription plans?" to "You can change plans in the store settings.",
        "What happens if i switch devices or platforms?" to "Restore your purchase on the new device.",
        "How does Rusthub Pro Lifetime plan work?" to "One time payment giving permanent access."
    )
    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        Text("FAQ", style = MaterialTheme.typography.titleMedium)
        faqs.forEach { (q, a) ->
            var expanded by remember { mutableStateOf(false) }
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(spacing.medium)
                        .animateContentSize(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(q, style = MaterialTheme.typography.titleSmall)
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }
                    AnimatedVisibility(expanded) {
                        Text(a, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = spacing.small))
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
