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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.CountryRegionMapper
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.presentation.features.server.ServerAction
import pl.cuyer.rusthub.presentation.model.FilterCheckboxOption
import pl.cuyer.rusthub.presentation.model.FilterDropdownOption
import pl.cuyer.rusthub.presentation.model.FilterRangeOption
import pl.cuyer.rusthub.presentation.model.FilterUi
import pl.cuyer.rusthub.util.StringProvider
import kotlin.text.toFloat
import kotlin.text.toInt
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    filters: FilterUi?,
    isLoadingFilters: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
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
                if (filters == null || (filters.lists.isEmpty() &&
                            filters.checkboxes.isEmpty() &&
                            filters.ranges.isEmpty())
                ) {
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
                            lists = filters.lists,
                            onListChange = { index, selected ->
                                onAction(ServerAction.OnDropdownChange(index, selected))
                            },
                            checkboxes = filters.checkboxes,
                            onCheckboxChange = { index, checked ->
                                onAction(ServerAction.OnCheckboxChange(index, checked))
                            },
                            ranges = filters.ranges,
                            onRangeChange = { index, value ->
                                onAction(ServerAction.OnRangeChange(index, value))
                            }
                        )
                        Spacer(Modifier.height(spacing.medium))
                        ButtonsSection(
                            modifier = Modifier.fillMaxWidth(),
                            onAction = onAction,
                            onDismiss = onDismiss
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

private fun getVirtualValueForIndex1(realValue: Float?, maxIntValue: Int): Float {
    return when (realValue?.toInt()) {
        1 -> 0f
        2 -> 1f
        3 -> 2f
        4 -> 3f
        5 -> 4f
        6 -> 5f
        maxIntValue -> 6f
        null -> 2f // Domyślna wartość (mapuje na 4)
        else -> 6f // Nieznane wartości mapuj na MAX
    }
}

private fun getRealValueForIndex1(virtualValue: Float, maxIntValue: Int): Int {
    return when (virtualValue.toInt()) {
        0 -> 1
        1 -> 2
        2 -> 3
        3 -> 4
        4 -> 5
        5 -> 6
        else -> maxIntValue // Pozycja 6 i wszystkie inne/nieoczekiwane mapuj na MAX
    }
}

private fun getVirtualValueForPlayerCount(realValue: Float?, maxIntValue: Int): Float {
    if (realValue == null) return 0f
    val steps = 9
    val stepSize = maxIntValue / steps.toFloat()
    val value = realValue.toInt()
    if (value >= maxIntValue) return steps.toFloat()
    return ((value / stepSize).toInt()).coerceIn(0, steps).toFloat()
}

private fun getRealValueForPlayerCount(virtualValue: Float, maxIntValue: Int): Int {
    val steps = 9
    val stepSize = maxIntValue / steps.toFloat()
    val idx = virtualValue.toInt().coerceIn(0, steps)
    return if (idx == steps) maxIntValue else (idx * stepSize).toInt()
}

private fun getVirtualValueForRanking(realValue: Float?, maxIntValue: Int): Float {
    if (realValue == null) return 0f
    val steps = 99
    val stepSize = maxIntValue / steps.toFloat()
    val value = realValue.toInt()
    if (value >= maxIntValue) return steps.toFloat()
    return ((value / stepSize).toInt()).coerceIn(0, steps).toFloat()
}

private fun getRealValueForRanking(virtualValue: Float, maxIntValue: Int): Int {
    val steps = 99
    val stepSize = maxIntValue / steps.toFloat()
    val idx = virtualValue.toInt().coerceIn(0, steps)
    return if (idx == steps) maxIntValue else (idx * stepSize).toInt()
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
private fun SingleRangeSlider(
    label: String,
    initialValue: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    formatValue: (Float) -> String,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val sliderState = rememberSliderState(
        value = initialValue,
        valueRange = valueRange,
        steps = steps
    )

    LaunchedEffect(sliderState) {
        snapshotFlow { sliderState.value }
            .debounce { 500 }
            .distinctUntilChanged()
            .collect { value ->
                onValueChange(value)
            }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Text(
            text = "$label: ${formatValue(sliderState.value)}",
            style = MaterialTheme.typography.bodyMedium
        )
        Slider(
            state = sliderState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeFilters(
    options: List<FilterRangeOption>,
    onOptionsChange: (Int, Int?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        options.forEachIndexed { index, option ->
            key(option.label) {
                val maxIntValue = option.max
                val maxValue = maxIntValue.toFloat()
                when (index) {
                    0 -> { // Player count slider
                        val steps = 9
                        val virtualValueRange = 0f..steps.toFloat()
                        val initialValue = remember(option.value, maxIntValue) {
                            getVirtualValueForPlayerCount(option.value?.toFloat(), maxIntValue)
                        }
                        SingleRangeSlider(
                            label = option.label,
                            initialValue = initialValue,
                            valueRange = virtualValueRange,
                            steps = steps,
                            formatValue = { virtualValue ->
                                getRealValueForPlayerCount(virtualValue, maxIntValue).toString()
                            },
                            onValueChange = { virtualValue ->
                                onOptionsChange(index, getRealValueForPlayerCount(virtualValue, maxIntValue))
                            }
                        )
                    }
                    2 -> { // Ranking slider
                        val steps = 99
                        val virtualValueRange = 0f..steps.toFloat()
                        val initialValue = remember(option.value, maxIntValue) {
                            getVirtualValueForRanking(option.value?.toFloat(), maxIntValue)
                        }
                        SingleRangeSlider(
                            label = option.label,
                            initialValue = initialValue,
                            valueRange = virtualValueRange,
                            steps = steps,
                            formatValue = { virtualValue ->
                                getRealValueForRanking(virtualValue, maxIntValue).toString()
                            },
                            onValueChange = { virtualValue ->
                                onOptionsChange(index, getRealValueForRanking(virtualValue, maxIntValue))
                            }
                        )
                    }
                    1 -> {
                        val steps = 5
                        val virtualValueRange = 0f..6f
                        val initialValue = remember(option.value, maxIntValue) {
                            getVirtualValueForIndex1(option.value?.toFloat(), maxIntValue)
                        }
                        SingleRangeSlider(
                            label = option.label,
                            initialValue = initialValue,
                            valueRange = virtualValueRange,
                            steps = steps,
                            formatValue = { virtualValue ->
                                getRealValueForIndex1(virtualValue, maxIntValue).toString()
                            },
                            onValueChange = { virtualValue ->
                                onOptionsChange(index, getRealValueForIndex1(virtualValue, maxIntValue))
                            }
                        )
                    }
                    else -> {
                        val initialValue = remember(option.value, maxValue, index) {
                            option.value?.toFloat() ?: maxValue
                        }
                        SingleRangeSlider(
                            label = option.label,
                            initialValue = initialValue,
                            valueRange = 0f..maxValue,
                            steps = 0,
                            formatValue = { realValue ->
                                realValue.toInt().toString()
                            },
                            onValueChange = { realValue ->
                                onOptionsChange(index, realValue.toInt())
                            }
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
    onDismiss: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.large, vertical = spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        AppButton(
            onClick = {
                onAction(ServerAction.OnClearFilters)
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth(),
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
                isLoadingFilters = false
            )
        }
    }
}

