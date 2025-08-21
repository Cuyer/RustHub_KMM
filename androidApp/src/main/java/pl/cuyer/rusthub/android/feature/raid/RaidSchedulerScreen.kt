@file:OptIn(
    ExperimentalFoundationApi::class,
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.navigation.ObserveAsEvents
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerAction
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerState
import pl.cuyer.rusthub.presentation.navigation.NavKey
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Clock

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

    val listState = rememberLazyListState()
    val raids = state.value.raids
    val selectedIds = state.value.selectedIds
    val selectionMode = selectedIds.isNotEmpty()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(RaidSchedulerAction.OnAddClick) }) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        },
        topBar = {
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
                            ) {
                                Icon(
                                    tint = contentColorFor(SearchBarDefaults.colors().containerColor),
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = delete,
                                )
                            }
                            IconButton(
                                enabled = selectedIds.size == 1,
                                onClick = { onAction(RaidSchedulerAction.OnEditSelected) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    tint = if (selectedIds.size == 1) contentColorFor(
                                        TopAppBarDefaults.topAppBarColors().containerColor) else contentColorFor(
                                        IconButtonDefaults.iconButtonColors().disabledContainerColor),
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = edit
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            itemsIndexed(raids, key = { _, raid -> raid.id }) { index, raid ->
                RaidItem(
                    raid = raid,
                    selected = raid.id in selectedIds,
                    selectionMode = selectionMode,
                    onLongClick = { onAction(RaidSchedulerAction.OnRaidLongClick(raid.id)) },
                    onClick = {
                        if (selectionMode) {
                            onAction(RaidSchedulerAction.OnRaidLongClick(raid.id))
                        }
                    },
                    onDismiss = { onAction(RaidSchedulerAction.OnRaidSwiped(raid.id)) }
                )
            }
        }
    }
}

@Composable
private fun RaidItem(
    raid: Raid,
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
            val timeLeft = remember(raid.dateTime) {
                val diff =
                    raid.dateTime.toInstant(TimeZone.currentSystemDefault()) - Clock.System.now()
                val minutes = diff.inWholeMinutes
                if (minutes <= 0) "0m" else {
                    val days = minutes / (60 * 24)
                    val hours = (minutes % (60 * 24)) / 60
                    val mins = minutes % 60
                    buildString {
                        if (days > 0) append("${days}d ")
                        if (hours > 0) append("${hours}h ")
                        append("${mins}m")
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium),
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
                    Column {
                        Text(raid.name, style = MaterialTheme.typography.titleMedium)
                        Text(raid.dateTime.toString(), style = MaterialTheme.typography.bodyMedium)
                        Text(raid.steamId, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            stringResource(SharedRes.strings.time_to_raid, timeLeft),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            stringResource(SharedRes.strings.estimated_raid_time, "1h"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    )
}

