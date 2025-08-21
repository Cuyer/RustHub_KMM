@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    kotlin.time.ExperimentalTime::class
)

package pl.cuyer.rusthub.android.feature.raid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import coil3.compose.SubcomposeAsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.clearAndSetSemantics
import coil3.request.ImageRequest
import coil3.request.crossfade
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.OnLifecycleEvent
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.model.SteamUser
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerAction
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerState
import pl.cuyer.rusthub.presentation.navigation.NavKey
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pl.cuyer.rusthub.android.designsystem.shimmer
import kotlin.time.Clock
import pl.cuyer.rusthub.util.formatLocalDateTime
import androidx.lifecycle.Lifecycle

@Composable
fun RaidSchedulerScreen(
    onNavigate: (NavKey) -> Unit,
    state: State<RaidSchedulerState>,
    onAction: (RaidSchedulerAction) -> Unit,
    uiEvent: Flow<UiEvent>,
) {
    ObserveAsEvents(uiEvent) { event ->
        if (event is UiEvent.Navigate) onNavigate(event.destination)
    }
    OnLifecycleEvent { event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            onAction(RaidSchedulerAction.OnRefresh)
        }
    }

    val listState = rememberLazyListState()
    val raids = state.value.raids
    val selectedIds = state.value.selectedIds
    val selectionMode = selectedIds.isNotEmpty()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(RaidSchedulerAction.OnAddClick) }) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(stringResource(SharedRes.strings.raids), fontWeight = FontWeight.SemiBold)
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = selectionMode,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessVeryLow
                                )
                            ) + scaleIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ),
                            exit = slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(durationMillis = 150)
                            )
                        ) {
                            Row {
                                val delete = stringResource(SharedRes.strings.delete)
                                val edit = stringResource(SharedRes.strings.edit)
                                IconButton(
                                    onClick = { onAction(RaidSchedulerAction.OnDeleteSelected) },
                                    modifier = Modifier.minimumInteractiveComponentSize(),
                                    enabled = selectedIds.isNotEmpty(),
                                    colors = IconButtonDefaults.iconButtonColors().copy(
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = delete)
                                }
                                IconButton(
                                    enabled = selectedIds.size == 1,
                                    onClick = { onAction(RaidSchedulerAction.OnEditSelected) },
                                    modifier = Modifier.minimumInteractiveComponentSize(),
                                    colors = IconButtonDefaults.iconButtonColors().copy(
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = edit)
                                }
                            }
                        }
                    }
                )
                AnimatedVisibility(
                    visible = !state.value.isConnected,
                    enter = slideInVertically(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    ),
                    exit = slideOutVertically(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    )
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = stringResource(SharedRes.strings.offline_cached_raids_info),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.xsmall)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = { onAction(RaidSchedulerAction.OnRefresh) },
            state = pullToRefreshState,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            when {
                state.value.isRefreshing -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(spacing.medium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(8) {
                            RaidItemShimmer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.medium)
                            )
                        }
                    }
                }
                state.value.hasError -> {
                    if (!state.value.isRefreshing) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            item("error") {
                                Column(
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
                }
                raids.isEmpty() -> {
                    if (!state.value.isRefreshing) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            item("empty") {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "( •_•)?",
                                        style = MaterialTheme.typography.headlineLarge,
                                        textAlign = TextAlign.Center,
                                        fontSize = 96.sp
                                    )
                                    val message = if (!state.value.isConnected) {
                                        stringResource(SharedRes.strings.no_raids_available_offline)
                                    } else {
                                        stringResource(SharedRes.strings.no_raids_available)
                                    }
                                    Text(
                                        text = message,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(spacing.medium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(raids, key = { _, raid -> raid.id }) { _, raid ->
                            RaidItem(
                                raid = raid,
                                users = state.value.users,
                                selected = raid.id in selectedIds,
                                selectionMode = selectionMode,
                                onLongClick = { onAction(RaidSchedulerAction.OnRaidLongClick(raid.id)) },
                                onClick = {
                                    if (selectionMode) {
                                        onAction(RaidSchedulerAction.OnRaidLongClick(raid.id))
                                    } else {
                                        onAction(RaidSchedulerAction.OnNavigateToRaid(raid))
                                    }
                                },
                                onDismiss = { onAction(RaidSchedulerAction.OnRaidSwiped(raid.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RaidItem(
    raid: Raid,
    users: Map<String, SteamUser?>,
    selected: Boolean,
    selectionMode: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error, shape = RectangleShape)
                    .padding(end = spacing.medium),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(SharedRes.strings.delete),
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        },
        onDismiss = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
            }
        },
        enableDismissFromStartToEnd = false,
        content = {
            val daysShort = stringResource(SharedRes.strings.days_short)
            val hoursShort = stringResource(SharedRes.strings.hours_short)
            val minutesShort = stringResource(SharedRes.strings.minutes_short)
            val timeLeft = remember(raid.dateTime, daysShort, hoursShort, minutesShort) {
                val diff =
                    raid.dateTime.toInstant(TimeZone.currentSystemDefault()) - Clock.System.now()
                val minutes = diff.inWholeMinutes
                if (minutes <= 0) null else {
                    val days = minutes / (60 * 24)
                    val hours = (minutes % (60 * 24)) / 60
                    val mins = minutes % 60
                    buildString {
                        if (days > 0) append("${days}$daysShort ")
                        if (hours > 0) append("${hours}$hoursShort ")
                        append("${mins}$minutesShort")
                    }
                }
            }
            ElevatedCard(
                shape = RectangleShape,
                colors = CardDefaults.elevatedCardColors().copy(
                    containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                    verticalAlignment = Alignment.Top,
                ) {
                    AnimatedVisibility(
                        visible = selectionMode,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessVeryLow
                            )
                        ) + scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                        exit = slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(durationMillis = 150)
                        ),
                    ) {
                        Checkbox(checked = selected, onCheckedChange = { onLongClick() })
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(SharedRes.strings.raid_data),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(spacing.small))
                        Text(raid.name, style = MaterialTheme.typography.titleMedium)
                        raid.description?.takeIf { it.isNotBlank() }?.let { desc ->
                            Text(
                                desc,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Text(
                            formatLocalDateTime(raid.dateTime),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = timeLeft?.let { stringResource(SharedRes.strings.time_to_raid, it) } ?: stringResource(SharedRes.strings.its_time_to_raid),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (raid.steamIds.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(spacing.small))
                            Text(
                                text = stringResource(SharedRes.strings.targets),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.height(spacing.small))
                            FlowRow(
                                maxItemsInEachRow = 3,
                                maxLines = 2,
                                horizontalArrangement = Arrangement.spacedBy(spacing.small, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(spacing.small)
                            ) {
                                raid.steamIds.take(6).forEach { id ->
                                    val user = users[id]
                                    if (user != null) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .height(IntrinsicSize.Max)
                                                .width(100.dp)
                                        ) {
                                            SubcomposeAsyncImage(
                                                modifier = Modifier.size(48.dp),
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(user.avatar)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "User avatar",
                                                loading = {
                                                    Box(
                                                        modifier = Modifier
                                                            .matchParentSize()
                                                            .shimmer()
                                                    )
                                                }
                                            )
                                            Text(
                                                user.personaName,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                            )
                                            val offline = stringResource(SharedRes.strings.offline)
                                            val online = stringResource(SharedRes.strings.online)
                                            val busy = stringResource(SharedRes.strings.busy)
                                            val away = stringResource(SharedRes.strings.away)
                                            val snooze = stringResource(SharedRes.strings.snooze)
                                            val lookingToTrade = stringResource(SharedRes.strings.looking_to_trade)
                                            val lookingToPlay = stringResource(SharedRes.strings.looking_to_play)
                                            val unknown = stringResource(SharedRes.strings.unknown)
                                            Text(
                                                text = when (user.personaState) {
                                                    0 -> offline
                                                    1 -> online
                                                    2 -> busy
                                                    3 -> away
                                                    4 -> snooze
                                                    5 -> lookingToTrade
                                                    6 -> lookingToPlay
                                                    else -> unknown
                                                },
                                                color = personaStateColor(user.personaState),
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    } else {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.width(100.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .shimmer(),
                                            )
                                            Spacer(modifier = Modifier.height(spacing.xsmall))
                                            Box(
                                                modifier = Modifier
                                                    .width(64.dp)
                                                    .height(16.dp)
                                                    .shimmer(),
                                            )
                                            Spacer(modifier = Modifier.height(spacing.xsmall))
                                            Box(
                                                modifier = Modifier
                                                    .width(32.dp)
                                                    .height(12.dp)
                                                    .shimmer(),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun RaidItemShimmer(modifier: Modifier = Modifier) {
    ElevatedCard(
        shape = RectangleShape,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(20.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
                    .clearAndSetSemantics {},
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
                    .clearAndSetSemantics {},
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
                    .clearAndSetSemantics {},
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
                    .clearAndSetSemantics {},
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .shimmer()
                            .clearAndSetSemantics {},
                    )
                }
            }
        }
    }
}

private fun personaStateColor(state: Int): Color = when (state) {
    1 -> Color(0xFF4CAF50)
    2 -> Color(0xFFF44336)
    3 -> Color(0xFFFFC107)
    else -> Color.Gray
}

