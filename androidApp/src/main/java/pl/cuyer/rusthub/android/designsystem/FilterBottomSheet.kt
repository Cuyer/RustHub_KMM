package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.presentation.features.ServerAction
import pl.cuyer.rusthub.presentation.features.ServerState
import pl.cuyer.rusthub.presentation.model.FilterUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    stateProvider: () -> State<ServerState>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAction: (ServerAction) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Text(
            text = "Filter Options",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(spacing.medium)
        )
        HorizontalDivider()
        FilterBottomSheetContent(filters = stateProvider().value.filters)
        Button(
            shape = RectangleShape,
            onClick = {
                /*onAction(
                    ServerAction.OnSaveFilters(filters = state.filters)
                )*/
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = spacing.medium, start = spacing.large, end = spacing.large)
        ) {
            Text("Apply Filters")
        }
        TextButton(
            shape = RectangleShape,
            onClick = {
                onAction(
                    ServerAction.OnClearFilters
                )
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.small, horizontal = spacing.large)
        ) {
            Text("Reset Filters")
        }
    }
}


@Composable
fun FilterBottomSheetContent(
    modifier: Modifier = Modifier,
    filters: FilterUi
) {
    Column(
        modifier = modifier
            .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        filters.lists.forEach { (label, options, selectedIndex) ->
            AppExposedDropdownMenu(
                label = label,
                options = options,
                selectedValue = selectedIndex ?: 0
            )
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            filters.checkboxes.forEach { (label, isChecked) ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SwitchWithText(
                        text = label,
                        isChecked = isChecked
                    )
                }
            }
        }

        filters.ranges.forEach { (label, max, value) ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "$label: $value", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = value.toFloat(),
                    onValueChange = { /* TODO: Handle slider change */ },
                    valueRange = 0f..max.toFloat(), // arbitrary max or make configurable
                    steps = 10
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun FilterBottomSheetPreview() {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    RustHubTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FilterBottomSheet(
                sheetState = sheetState,
                onDismiss = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                },
                stateProvider = { mutableStateOf(ServerState()) },
                onAction = { }
            )
        }
    }
}