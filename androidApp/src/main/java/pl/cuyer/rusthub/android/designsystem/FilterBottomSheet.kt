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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import pl.cuyer.rusthub.android.designsystem.shimmer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState
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
    filters: FilterUi?,
    isLoadingFilters: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onDismissAndRefresh: () -> Unit,
    onAction: (ServerAction) -> Unit
) {
    // Use rememberSaveable with manual restoration for navigation or recomposition
    var localLists by rememberSaveable(inputs = arrayOf(filters?.lists)) {
        mutableStateOf(filters?.lists ?: emptyList())
    }
    var localCheckboxes by rememberSaveable(inputs = arrayOf(filters?.checkboxes)) {
        mutableStateOf(filters?.checkboxes ?: emptyList())
    }
    var localRanges by rememberSaveable(inputs = arrayOf(filters?.ranges)) {
        mutableStateOf(filters?.ranges ?: emptyList())
    }

    // When filters are loaded/reset, update local state (but do not reset on every parent recomposition!)
    LaunchedEffect(filters) {
        if (filters != null) {
            localLists = filters.lists
            localCheckboxes = filters.checkboxes
            localRanges = filters.ranges
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
            targetState = isLoadingFilters,
            transitionSpec = { defaultFadeTransition() }
        ) { loading ->
            if (loading) {
                FilterBottomSheetShimmer(
                    modifier = Modifier.fillMaxHeight(0.85f)
                )
            } else {
                if (localLists.isEmpty() && localCheckboxes.isEmpty() && localRanges.isEmpty()) {
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
                        // Pass local state and their updaters
                        FilterBottomSheetContent(
                            lists = { localLists },
                            onListsChange = { localLists = it },
                            checkboxes = { localCheckboxes },
                            onCheckboxesChange = { localCheckboxes = it },
                            ranges = { localRanges },
                            onRangesChange = { localRanges = it }
                        )
                        Spacer(Modifier.height(spacing.medium))
                        ButtonsSection(
                            modifier = Modifier.fillMaxWidth(),
                            onAction = onAction,
                            onDismissAndRefresh = onDismissAndRefresh,
                            filters = { FilterUi(localLists, localCheckboxes, localRanges) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterBottomSheetContent(
    lists: () -> List<FilterDropdownOption>,
    onListsChange: (List<FilterDropdownOption>) -> Unit,
    checkboxes: () -> List<FilterCheckboxOption>,
    onCheckboxesChange: (List<FilterCheckboxOption>) -> Unit,
    ranges: () -> List<FilterRangeOption>,
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
    options: () -> List<FilterDropdownOption>,
    onOptionsChange: (List<FilterDropdownOption>) -> Unit
) {
    options().forEachIndexed { index, option ->
        key(option.label) {
            AppExposedDropdownMenu(
                label = option.label,
                options = option.options,
                selectedValue = option.selectedIndex ?: 0,
                onSelectionChanged = { selected ->
                    val updated = options().toMutableList()
                    updated[index] = option.copy(selectedIndex = selected)
                    onOptionsChange(updated)
                }
            )
        }
    }
}

@Composable
private fun CheckboxFilters(
    options: () -> List<FilterCheckboxOption>,
    onOptionsChange: (List<FilterCheckboxOption>) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        options().forEachIndexed { index, option ->
            key(option.label) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var checked by rememberSaveable(option.label) { mutableStateOf(option.isChecked) }
                    SwitchWithText(
                        text = option.label,
                        isChecked = { checked },
                        onCheckedChange = { newChecked ->
                            checked = newChecked
                            val updated = options().toMutableList()
                            updated[index] = option.copy(isChecked = newChecked)
                            onOptionsChange(updated)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RangeFilters(
    options: () -> List<FilterRangeOption>,
    onOptionsChange: (List<FilterRangeOption>) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardState = keyboardAsState()
    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        options().forEachIndexed { index, option ->
            key(option.label) {
                val textFieldState = rememberTextFieldState(
                    initialText = option.value?.toString() ?: ""
                )

                // Keep TextFieldState in sync with option.value changes (if they come from parent)
                LaunchedEffect(option.value) {
                    val text = option.value?.toString() ?: ""
                    if (text != textFieldState.text.toString()) {
                        textFieldState.setTextAndPlaceCursorAtEnd(text)
                    }
                }

                // Propagate changes to parent state only when text changes
                // (You can also validate here or debounce if needed)
                LaunchedEffect(textFieldState.text) {
                    val newValue = textFieldState.text.toString().toIntOrNull()
                    if (newValue != option.value) {
                        val updated = options().toMutableList()
                        updated[index] = option.copy(value = newValue)
                        onOptionsChange(updated)
                    }
                }

                AppTextField(
                    modifier = Modifier,
                    textFieldState = textFieldState,
                    labelText = option.label,
                    maxLength = option.max.toString().length,
                    placeholderText = stringResource(SharedRes.strings.enter_a_number),
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                    focusManager = focusManager,
                    keyboardState = keyboardState
                )
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
private fun FilterBottomSheetShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(40.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmer()
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
                filters = null,
                onAction = { },
                isLoadingFilters = false,
                onDismissAndRefresh = { }
            )
        }
    }
}
