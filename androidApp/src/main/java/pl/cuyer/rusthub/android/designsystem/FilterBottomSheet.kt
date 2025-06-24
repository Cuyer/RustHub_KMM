package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.designsystem.AppButton
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import pl.cuyer.rusthub.presentation.features.server.ServerAction
import pl.cuyer.rusthub.presentation.features.server.ServerState
import pl.cuyer.rusthub.presentation.model.FilterUi
import pl.cuyer.rusthub.presentation.model.toDomain

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    stateProvider: () -> State<ServerState>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDismissAndRefresh: () -> Unit,
    onAction: (ServerAction) -> Unit
) {
    val json = koinInject<Json>()
    val filterUiSaver = remember {
        Saver<FilterUi?, String>(
            save = { it?.let { json.encodeToString(FilterUi.serializer(), it) } },
            restore = { it.let { json.decodeFromString(FilterUi.serializer(), it) } }
        )
    }
    var newFilters by rememberSaveable(stateSaver = filterUiSaver) {
        mutableStateOf(stateProvider().value.filters)
    }

    LaunchedEffect(stateProvider().value.filters) {
        if (newFilters == null && stateProvider().value.filters?.lists?.isNotEmpty() == true) {
            newFilters = stateProvider().value.filters
        }
    }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier.bottomSheetNestedScroll(sheetState) { velocity ->
            coroutineScope.launch { sheetState.settleCompat(velocity) }
                .invokeOnCompletion { if (!sheetState.isVisible) onDismiss() }
        }
    ) {
        Text(
            text = "Filter Options",
            style = MaterialTheme.typography.titleLargeEmphasized,
            modifier = Modifier
                .padding(start = spacing.medium, end = spacing.medium, bottom = spacing.medium)
        )
        HorizontalDivider()
        AnimatedContent(stateProvider().value.isLoadingFilters) { loading ->
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                if (stateProvider().value.filters?.lists?.isEmpty() == true) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.85f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No filters available",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(0.85f)
                            .verticalScroll(scrollState)
                    ) {
                        newFilters?.let {
                            FilterBottomSheetContent(
                                filters = it,
                                onFiltersChange = { newFilters = it }
                            )
                            Spacer(Modifier.height(spacing.medium))
                            AppButton(
                                onClick = {
                                    onAction(ServerAction.OnSaveFilters(filters = it.toDomain()))
                                    onDismissAndRefresh()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.large)
                            ) {
                                Text("Apply Filters")
                            }
                            AppTextButton(
                                onClick = {
                                    onAction(ServerAction.OnClearFilters)
                                    onDismissAndRefresh()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacing.small, horizontal = spacing.large)
                            ) {
                                Text(
                                    text = "Reset Filters",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FilterBottomSheetContent(
    modifier: Modifier = Modifier,
    filters: FilterUi,
    onFiltersChange: (FilterUi) -> Unit
) {
    Column(
        modifier = modifier
            .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        filters.lists.forEachIndexed { index, (label, options, selectedIndex) ->
            AppExposedDropdownMenu(
                label = label,
                options = options,
                selectedValue = selectedIndex ?: 0,
                onSelectionChanged = { selected ->
                    val updated = filters.lists.toMutableList()
                    val old = updated[index]
                    updated[index] = Triple(old.first, old.second, selected)
                    onFiltersChange(filters.copy(lists = updated))
                }
            )
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            filters.checkboxes.forEachIndexed { index, (label, isChecked) ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SwitchWithText(
                        text = label,
                        isChecked = isChecked,
                        onCheckedChange = { checked ->
                            val updated = filters.checkboxes.toMutableList()
                            updated[index] = label to checked
                            onFiltersChange(filters.copy(checkboxes = updated))
                        }
                    )
                }
            }
        }


        filters.ranges.forEachIndexed { index, (label, max, value) ->
            val textFieldState =
                remember(value) { TextFieldState(initialText = value?.toString() ?: "") }
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
            LaunchedEffect(textFieldState.text) {
                val updated = filters.ranges.toMutableList()
                val newValue = textFieldState.text.toString().toIntOrNull()
                val old = updated[index]
                updated[index] = Triple(old.first, old.second, newValue)
                onFiltersChange(filters.copy(ranges = updated))
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
                stateProvider = { mutableStateOf(ServerState(isLoading = true)) },
                onAction = { },
                onDismissAndRefresh = { }
            )
        }
    }
}