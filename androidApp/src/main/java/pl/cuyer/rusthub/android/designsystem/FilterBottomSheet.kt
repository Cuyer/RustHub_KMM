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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.keyboardAsState
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.domain.model.CountryRegionMapper
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.presentation.features.server.ServerAction
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
                val current = filters
                if (current == null || (current.lists.isEmpty() &&
                        current.checkboxes.isEmpty() &&
                        current.ranges.isEmpty())) {
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
                            lists = current.lists,
                            onListChange = { index, selected ->
                                onAction(ServerAction.OnDropdownChange(index, selected))
                            },
                            checkboxes = current.checkboxes,
                            onCheckboxChange = { index, checked ->
                                onAction(ServerAction.OnCheckboxChange(index, checked))
                            },
                            ranges = current.ranges,
                            onRangeChange = { index, value ->
                                onAction(ServerAction.OnRangeChange(index, value))
                            }
                        )
                        Spacer(Modifier.height(spacing.medium))
                        ButtonsSection(
                            modifier = Modifier.fillMaxWidth(),
                            onAction = onAction,
                            onDismissAndRefresh = onDismissAndRefresh,
                            filters = current
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterBottomSheetContent(
    lists: List<FilterDropdownOption>,
    onListChange: (Int, Int?) -> Unit,
    checkboxes: List<FilterCheckboxOption>,
    onCheckboxChange: (Int, Boolean) -> Unit,
    ranges: List<FilterRangeOption>,
    onRangeChange: (Int, Int?) -> Unit
) {
    Column(
        modifier = Modifier.padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        DropdownFilters(
            options = lists,
            onOptionChange = onListChange
        )
        CheckboxFilters(
            options = checkboxes,
            onOptionChange = onCheckboxChange
        )
        RangeFilters(
            options = ranges,
            onOptionsChange = onRangeChange
        )
    }
}

@Composable
private fun DropdownFilters(
    options: List<FilterDropdownOption>,
    onOptionChange: (Int, Int?) -> Unit
) {
    val stringProvider = koinInject<StringProvider>()
    val selectedCountry = options.getOrNull(1)?.let { opt ->
        opt.selectedIndex?.let { idx ->
            opt.options.getOrNull(idx)?.let { Flag.fromDisplayName(it) }
        }
    }
    val selectedRegion = options.getOrNull(2)?.let { opt ->
        opt.selectedIndex?.let { idx ->
            opt.options.getOrNull(idx)?.let { Region.fromDisplayName(it, stringProvider) }
        }
    }
    options.forEachIndexed { index, option ->
        key(option.label) {
            val enabledForIndex: (Int) -> Boolean = when (index) {
                1 -> { idx ->
                    selectedRegion?.let { region ->
                        Flag.fromDisplayName(option.options[idx])?.let { flag ->
                            CountryRegionMapper.regionForFlag(flag) == region
                        } ?: false
                    } ?: true
                }
                2 -> { idx ->
                    selectedCountry?.let { flag ->
                        val countryRegion = CountryRegionMapper.regionForFlag(flag)
                        val region = Region.fromDisplayName(option.options[idx], stringProvider)
                        countryRegion == region
                    } ?: true
                }
                else -> { _ -> true }
            }
            AppExposedDropdownMenu(
                label = option.label,
                options = option.options,
                selectedValue = option.selectedIndex,
                onSelectionChanged = { selected ->
                    onOptionChange(index, selected)
                },
                allowEmptySelection = true,
                enabledForIndex = enabledForIndex
            )
        }
    }
}

@Composable
private fun CheckboxFilters(
    options: List<FilterCheckboxOption>,
    onOptionChange: (Int, Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        options.forEachIndexed { index, option ->
            key(option.label) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SwitchWithText(
                        text = option.label,
                        isChecked = { option.isChecked },
                        onCheckedChange = { newChecked ->
                            onOptionChange(index, newChecked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RangeFilters(
    options: List<FilterRangeOption>,
    onOptionsChange: (Int, Int?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardState = keyboardAsState()
    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        options.forEachIndexed { index, option ->
            key(option.label) {
                val textFieldState = rememberTextFieldState(
                    initialText = option.value?.toString() ?: ""
                )

                LaunchedEffect(option.value) {
                    val text = option.value?.toString() ?: ""
                    if (text != textFieldState.text.toString()) {
                        textFieldState.setTextAndPlaceCursorAtEnd(text)
                    }
                }

                LaunchedEffect(textFieldState.text) {
                    val newValue = textFieldState.text.toString().toIntOrNull()
                    if (newValue != option.value) {
                        onOptionsChange(index, newValue)
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
    filters: FilterUi
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.large, vertical = spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        val stringProvider = koinInject<StringProvider>()
        AppButton(
            onClick = {
                onAction(ServerAction.OnSaveFilters(filters.toDomain(stringProvider)))
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
                filters = FilterUi(),
                onAction = { },
                isLoadingFilters = false,
                onDismissAndRefresh = { }
            )
        }
    }
}

