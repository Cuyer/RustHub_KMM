package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismiss: () -> Unit
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
        FilterBottomSheetContent(
            filtersMap = mapOf(
                "Map" to Maps.entries.map { it.name },
                "Country" to Flag.entries.map { it.name },
                "Region" to Region.entries.map { it.name },
                "Difficulty" to Difficulty.entries.map { it.name },
                "Wipe Schedule" to WipeSchedule.entries.map { it.name }
            )
        )
        Button(
            shape = RectangleShape,
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = spacing.medium, start = spacing.large, end = spacing.large)
        ) {
            Text("Apply Filters")
        }
    }
}


@Composable
fun FilterBottomSheetContent(
    modifier: Modifier = Modifier,
    filtersMap: Map<String, List<String>>
) {
    Column(
        modifier = modifier
            .padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        filtersMap.forEach {
            AppExposedDropdownMenu(it.key, it.value)
        }
        SwitchRow()
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
                }
            )
        }
    }
}