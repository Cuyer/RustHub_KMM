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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.presentation.features.server.ServerAction
import pl.cuyer.rusthub.presentation.features.server.ServerState
import pl.cuyer.rusthub.presentation.model.FilterCheckboxOption
import pl.cuyer.rusthub.presentation.model.FilterDropdownOption
import pl.cuyer.rusthub.presentation.model.FilterRangeOption
import pl.cuyer.rusthub.presentation.model.FilterUi
import pl.cuyer.rusthub.presentation.model.toDomain
import pl.cuyer.rusthub.util.StringProvider

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    state: State<ServerState>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDismissAndRefresh: () -> Unit,
    onAction: (ServerAction) -> Unit
) {
    val json = koinInject<Json>()
    val dropdownSaver = remember {
        Saver<List<FilterDropdownOption>, String>(
            save = {
                json.encodeToString(
                    ListSerializer(FilterDropdownOption.serializer()),
                    it
                )
            },
            restore = {
                json.decodeFromString(
                    ListSerializer(FilterDropdownOption.serializer()),
                    it
                )
            }
        )
    }
    val checkboxSaver = remember {
        Saver<List<FilterCheckboxOption>, String>(
            save = {
                json.encodeToString(
                    ListSerializer(FilterCheckboxOption.serializer()),
                    it
                )
            },
            restore = {
                json.decodeFromString(
                    ListSerializer(FilterCheckboxOption.serializer()),
                    it
                )
            }
        )
    }
    val rangeSaver = remember {
        Saver<List<FilterRangeOption>, String>(
            save = {
                json.encodeToString(
                    ListSerializer(FilterRangeOption.serializer()),
                    it
                )
            },
            restore = {
                json.decodeFromString(
                    ListSerializer(FilterRangeOption.serializer()),
                    it
                )
            }
        )
    }

    var lists by rememberSaveable(stateSaver = dropdownSaver) {
        mutableStateOf(state.value.filters?.lists ?: emptyList())
    }
    var checkboxes by rememberSaveable(stateSaver = checkboxSaver) {
        mutableStateOf(state.value.filters?.checkboxes ?: emptyList())
    }
    var ranges by rememberSaveable(stateSaver = rangeSaver) {
        mutableStateOf(state.value.filters?.ranges ?: emptyList())
    }

    LaunchedEffect(state.value.filters) {
        val filters = state.value.filters
        if (lists.isEmpty() && filters?.lists?.isNotEmpty() == true) {
            lists = filters.lists
            checkboxes = filters.checkboxes
            ranges = filters.ranges
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
        val header = stringResource(SharedRes.strings.filter_options)
        Text(
            text = header,
            style = MaterialTheme.typography.titleLargeEmphasized,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = spacing.medium)
                .semantics {
                    heading()
                    contentDescription = header
                }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.medium))
        AnimatedContent(
            targetState = state.value.isLoadingFilters,
            transitionSpec = { defaultFadeTransition() }
        ) { loading ->
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
                if (state.value.filters?.lists?.isEmpty() == true) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.85f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(SharedRes.strings.no_filters_available),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(0.85f)
                            .verticalScroll(scrollState)
                    ) {
                        FilterBottomSheetContent(
                            lists = lists,
                            onListsChange = { lists = it },
                            checkboxes = checkboxes,
                            onCheckboxesChange = { checkboxes = it },
                            ranges = ranges,
                            onRangesChange = { ranges = it }
                        )
                        Spacer(Modifier.height(spacing.medium))
                        ButtonsSection(
                            modifier = Modifier.fillMaxWidth(),
                            onAction = onAction,
                            onDismissAndRefresh = onDismissAndRefresh,
                            filters = { FilterUi(lists, checkboxes, ranges) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonsSection(
    modifier: Modifier = Modifier,
    onAction: (ServerAction) -> Unit,
    onDismissAndRefresh: () -> Unit,
    filters: () -> FilterUi
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.large, vertical = spacing.small)
    ) {
        val stringProvider = koinInject<StringProvider>()
        AppButton(
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ),
            onClick = {
                onAction(ServerAction.OnSaveFilters(filters = filters().toDomain(stringProvider)))
                onDismissAndRefresh()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(SharedRes.strings.apply_filters))
        }
        AppOutlinedButton(
            colors = ButtonDefaults.outlinedButtonColors().copy(
                contentColor = contentColorFor(BottomSheetDefaults.ContainerColor)
            ),
            onClick = {
                onAction(ServerAction.OnClearFilters)
                onDismissAndRefresh()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(SharedRes.strings.reset_filters),
            )
        }
    }
}


@Composable
fun FilterBottomSheetContent(
    lists: List<FilterDropdownOption>,
    onListsChange: (List<FilterDropdownOption>) -> Unit,
    checkboxes: List<FilterCheckboxOption>,
    onCheckboxesChange: (List<FilterCheckboxOption>) -> Unit,
    ranges: List<FilterRangeOption>,
    onRangesChange: (List<FilterRangeOption>) -> Unit
) {
    Column(
        modifier = Modifier.padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        DropdownFilters(
            options = lists,
            onOptionsChange = onListsChange
        )
        CheckboxFilters(
            options = checkboxes,
            onOptionsChange = onCheckboxesChange
        )
        RangeFilters(
            options = ranges,
            onOptionsChange = onRangesChange
        )
    }
}

@Composable
private fun DropdownFilters(
    options: List<FilterDropdownOption>,
    onOptionsChange: (List<FilterDropdownOption>) -> Unit
) {
    options.forEachIndexed { index, option ->
        AppExposedDropdownMenu(
            label = option.label,
            options = option.options,
            selectedValue = option.selectedIndex ?: 0,
            onSelectionChanged = { selected ->
                val updated = options.toMutableList()
                updated[index] = option.copy(selectedIndex = selected)
                onOptionsChange(updated)
            }
        )
    }
}

@Composable
private fun CheckboxFilters(
    options: List<FilterCheckboxOption>,
    onOptionsChange: (List<FilterCheckboxOption>) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        options.forEachIndexed { index, option ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SwitchWithText(
                    text = option.label,
                    isChecked = { option.isChecked },
                    onCheckedChange = { checked ->
                        val updated = options.toMutableList()
                        updated[index] = option.copy(isChecked = checked)
                        onOptionsChange(updated)
                    }
                )
            }
        }
    }
}

@Composable
private fun RangeFilters(
    options: List<FilterRangeOption>,
    onOptionsChange: (List<FilterRangeOption>) -> Unit
) {
    options.forEachIndexed { index, option ->
        AppTextField(
            modifier = Modifier,
            value = option.value?.toString() ?: "",
            labelText = option.label,
            maxLength = option.max.toString().length,
            placeholderText = stringResource(SharedRes.strings.enter_a_number),
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done,
            onValueChange = { text ->
                val updated = options.toMutableList()
                val newValue = text.toIntOrNull()
                updated[index] = option.copy(value = newValue)
                onOptionsChange(updated)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun FilterBottomSheetPreview() {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val state = remember { mutableStateOf(ServerState(loadingMore = true)) }
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
                state = state,
                onAction = { },
                onDismissAndRefresh = { }
            )
        }
    }
}