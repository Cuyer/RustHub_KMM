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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pl.cuyer.rusthub.SharedRes
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

    // Copy raids into a mutable list for UI reordering
    val raids = remember(state.value.raids) { mutableStateListOf(*state.value.raids.toTypedArray()) }

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
        bottomBar = {
            if (state.value.selectedIds.isNotEmpty()) {
                val delete = stringResource(SharedRes.strings.delete)
                val edit = stringResource(SharedRes.strings.edit)
                ButtonGroup(
                    overflowIndicator = {
                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                    }
                ) {
                    clickableItem(
                        onClick = { onAction(RaidSchedulerAction.OnDeleteSelected) },
                        label = delete,
                        icon = { Icon(Icons.Filled.Delete, contentDescription = null) }
                    )
                    clickableItem(
                        onClick = { onAction(RaidSchedulerAction.OnEditSelected) },
                        label = edit,
                        icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                        enabled = state.value.selectedIds.size == 1
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(padding)
        ) {
            itemsIndexed(state.value.raids, key = { _, raid -> raid.id }) { index, raid ->
                RaidItem(
                    raid = raid,
                    selected = raid.id in state.value.selectedIds,
                    selectionMode = state.value.selectedIds.isNotEmpty(),
                    onLongClick = { onAction(RaidSchedulerAction.OnRaidLongClick(raid.id)) },
                    onClick = {
                        if (state.value.selectedIds.isNotEmpty()) {
                            onAction(RaidSchedulerAction.OnRaidLongClick(raid.id))
                        }
                    },
                    onDismiss = { onAction(RaidSchedulerAction.OnRaidSwiped(raid.id)) },
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress { change, _ ->
                                change.consume()
                            }
                        }
                        .dragAndDropSource(
                            transferData = { _: Offset ->
                                DragAndDropTransferData(
                                    ClipData.newPlainText("id", raid.id),
                                    flags = View.DRAG_FLAG_GLOBAL,
                                )
                            }
                        )
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { true },
                            target = object : DragAndDropTarget {
                                override fun onDrop(event: DragAndDropEvent): Boolean {
                                    val fromIndex = raids.indexOfFirst { it.id == raid.id }
                                    if (fromIndex != -1 && fromIndex != index) {
                                        val moved = raids.removeAt(fromIndex)
                                        raids.add(index, moved)
                                        onAction(RaidSchedulerAction.OnMoveRaid(fromIndex, index))
                                    }
                                    return true
                                }
                            }
                        )
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
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                    .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                    .padding(16.dp)
            ) {
                if (selectionMode) {
                    Checkbox(checked = selected, onCheckedChange = { onLongClick() })
                    Spacer(modifier = Modifier.width(16.dp))
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
