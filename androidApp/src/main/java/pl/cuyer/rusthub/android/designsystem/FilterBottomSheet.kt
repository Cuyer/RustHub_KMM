package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.presentation.features.ServerAction
import pl.cuyer.rusthub.presentation.features.ServerState
import pl.cuyer.rusthub.presentation.model.FilterUi
import pl.cuyer.rusthub.presentation.model.toDomain

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    stateProvider: () -> State<ServerState>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAction: (ServerAction) -> Unit,
) {
    val scrollState = rememberScrollState()

    var newFilters by remember { mutableStateOf(FilterUi()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Text(
            text = "Filter Options",
            style = MaterialTheme.typography.titleLargeEmphasized,
            modifier = Modifier.padding(spacing.medium)
        )
        HorizontalDivider()
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .verticalScroll(scrollState)
        ) {
            FilterBottomSheetContent(
                filters = stateProvider().value.filters
            )
            Button(
                shape = RectangleShape,
                onClick = {
                    onAction(
                        ServerAction.OnSaveFilters(filters = newFilters.toDomain())
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.medium, start = spacing.large, end = spacing.large)
            ) {
                Text(
                    text = "Apply Filters",
                )
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
                Text(
                    text = "Reset Filters"
                )
            }
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
                selectedValue = selectedIndex ?: 0,
                onSelectionChanged = { index ->

                }
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
            val textFieldState = remember { TextFieldState(initialText = value?.toString() ?: "") }
            OutlinedTextField(
                state = textFieldState,
                label = {
                    Text(
                        text = label
                    )
                },
                inputTransformation = InputTransformation.maxLength(max.toString().length),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                placeholder = {
                    Text(
                        text = "Enter a number"
                    )
                }
            )
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