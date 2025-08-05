package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Flag.Companion.toDrawable

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AppExposedDropdownMenu(
    label: String,
    options: List<String>,
    selectedValue: Int?,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisibleItems: Int = 6
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(options.getOrElse(selectedValue ?: -1) { "" })
    val focusManager = LocalFocusManager.current
    val flagsByOption = remember(options) { options.associateWith { Flag.fromDisplayName(it) } }
    var textFieldWidth by remember { mutableIntStateOf(0) }

    // Set text on selection change
    LaunchedEffect(selectedValue) {
        textFieldState.setTextAndPlaceCursorAtEnd(options.getOrElse(selectedValue ?: -1) { "" })
    }
    LaunchedEffect(expanded) {
        if (!expanded) focusManager.clearFocus()
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .onSizeChanged { textFieldWidth = it.width },
            state = textFieldState,
            readOnly = true,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text(label) },
            trailingIcon = {
                val description = if (expanded) stringResource(SharedRes.strings.expanded)
                else stringResource(SharedRes.strings.collapsed)
                Box(Modifier.semantics { contentDescription = description }) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalResources.current.displayMetrics) { textFieldWidth / density }.dp)
        ) {
            // Calculate dropdown height
            val visibleItems = options.size.coerceAtMost(maxVisibleItems)
            val dropdownHeight = visibleItems * 48.dp
            // Menu with LazyColumn
            LazyColumn(
                modifier = Modifier
                    .width(with(LocalResources.current.displayMetrics) { textFieldWidth / density }.dp)
                    .height(dropdownHeight)
            ) {
                itemsIndexed(
                    items = options,
                    key = { index, option -> "${option}_$index" }
                ) { index, option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd(option)
                            expanded = false
                            onSelectionChanged(index)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        trailingIcon = {
                            flagsByOption[option]?.let { flag ->
                                if (label != stringResource(SharedRes.strings.region)) {
                                    Image(
                                        painter = painterResource(flag.toDrawable()),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp).clearAndSetSemantics {}
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}