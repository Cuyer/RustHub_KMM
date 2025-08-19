@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    kotlin.time.ExperimentalTime::class
)

package pl.cuyer.rusthub.android.feature.raid

import android.content.ClipData
import android.view.DragAndDropPermissions
import android.view.View
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.defaultFadeTransition
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerAction
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerState

@Composable
fun RaidSchedulerScreen(
    state: State<RaidSchedulerState>,
    onAction: (RaidSchedulerAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()
    val raids = state.value.raids
    val selectedIds = state.value.selectedIds
    val selectionMode = selectedIds.isNotEmpty()

    if (state.value.showForm) {
        ModalBottomSheet(
            onDismissRequest = { onAction(RaidSchedulerAction.OnDismissForm) },
            sheetState = sheetState
        ) {
            RaidForm(
                raid = state.value.editingRaid,
                onSave = { onAction(RaidSchedulerAction.OnSaveRaid(it)) }
            )
        }
    }

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
            contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
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
                    onDismiss = { onAction(RaidSchedulerAction.OnRaidSwiped(raid.id)) },
                    modifier = Modifier
                        .padding(horizontal = spacing.xmedium)
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
        backgroundContent = {},
        onDismiss = {
            if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
            }
        },
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
                shape = MaterialTheme.shapes.extraSmall,
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
                        Text(raid.target, style = MaterialTheme.typography.bodyMedium)
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

@Composable
private fun RaidForm(
    raid: Raid?,
    onSave: (Raid) -> Unit,
) {
    var name by remember { mutableStateOf(raid?.name.orEmpty()) }
    var date by remember { mutableStateOf(raid?.dateTime.toString()) }
    var target by remember { mutableStateOf(raid?.target.orEmpty()) }
    var description by remember { mutableStateOf(raid?.description.orEmpty()) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(SharedRes.strings.raid_name)) }
        )
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text(stringResource(SharedRes.strings.raid_date_time)) }
        )
        OutlinedTextField(
            value = target,
            onValueChange = { target = it },
            label = { Text(stringResource(SharedRes.strings.raid_target)) }
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(SharedRes.strings.description)) }
        )
        Button(
            onClick = {
                val id = raid?.id ?: Clock.System.now().toEpochMilliseconds().toString()
                val dateTime = try {
                    LocalDateTime.parse(date)
                } catch (e: Exception) {
                    LocalDateTime.parse("1970-01-01T00:00")
                }
                onSave(
                    Raid(
                        id = id,
                        name = name,
                        dateTime = dateTime,
                        target = target,
                        description = description.ifBlank { null }
                    )
                )
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(stringResource(SharedRes.strings.save))
        }
    }
}
