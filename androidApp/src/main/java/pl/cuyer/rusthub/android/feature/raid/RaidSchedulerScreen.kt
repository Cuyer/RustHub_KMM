package pl.cuyer.rusthub.android.feature.raid

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.ReorderableItem
import androidx.compose.foundation.lazy.rememberReorderableLazyListState
import androidx.compose.foundation.lazy.reorderable
import androidx.compose.foundation.lazy.detectReorderAfterLongPress
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerAction
import pl.cuyer.rusthub.presentation.features.raid.RaidSchedulerState
import pl.cuyer.rusthub.SharedRes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RaidSchedulerScreen(
    state: RaidSchedulerState,
    onAction: (RaidSchedulerAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (state.showForm) {
        ModalBottomSheet(
            onDismissRequest = { onAction(RaidSchedulerAction.OnDismissForm) },
            sheetState = sheetState
        ) {
            RaidForm(
                raid = state.editingRaid,
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
            if (state.selectedIds.isNotEmpty()) {
                ButtonGroup(
                    overflowIndicator = {
                        Icon(Icons.Filled.MoreVert, contentDescription = null)
                    }
                ) {
                    Button(onClick = { onAction(RaidSchedulerAction.OnDeleteSelected) }) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(SharedRes.strings.delete))
                    }
                    Button(
                        onClick = { onAction(RaidSchedulerAction.OnEditSelected) },
                        enabled = state.selectedIds.size == 1
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(SharedRes.strings.edit))
                    }
                }
            }
        }
    ) { padding ->
        val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
            onAction(RaidSchedulerAction.OnMoveRaid(from.index, to.index))
        })
        LazyColumn(
            state = reorderState.listState,
            modifier = Modifier
                .padding(padding)
                .reorderable(reorderState)
                .detectReorderAfterLongPress(reorderState)
        ) {
            items(state.raids, key = { it.id }) { raid ->
                ReorderableItem(reorderState, key = raid.id) { _ ->
                    RaidItem(
                        raid = raid,
                        selected = raid.id in state.selectedIds,
                        selectionMode = state.selectedIds.isNotEmpty(),
                        onLongClick = { onAction(RaidSchedulerAction.OnRaidLongClick(raid.id)) },
                        onClick = {
                            if (state.selectedIds.isNotEmpty()) {
                                onAction(RaidSchedulerAction.OnRaidLongClick(raid.id))
                            }
                        },
                        onDismiss = { onAction(RaidSchedulerAction.OnRaidSwiped(raid.id)) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RaidItem(
    raid: Raid,
    selected: Boolean,
    selectionMode: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onDismiss: () -> Unit
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
                val diff = raid.dateTime.toInstant(TimeZone.currentSystemDefault()) - Clock.System.now()
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
                modifier = Modifier
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
                    Text(stringResource(SharedRes.strings.time_to_raid, timeLeft), style = MaterialTheme.typography.bodyMedium)
                    Text(stringResource(SharedRes.strings.estimated_raid_time, "1h"), style = MaterialTheme.typography.bodyMedium)
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
        Button(onClick = {
            val id = raid?.id ?: kotlinx.datetime.Clock.System.now().toEpochMilliseconds().toString()
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
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(SharedRes.strings.save))
        }
    }
}
