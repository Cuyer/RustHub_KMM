package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.min
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Flag.Companion.toDrawable
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AppExposedDropdownMenu(
    label: String,
    options: List<String>,
    selectedValue: Int?,
    onSelectionChanged: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(options.getOrElse(selectedValue ?: -1) { "" })
    val focusManager = LocalFocusManager.current

    LaunchedEffect(selectedValue) {
        textFieldState.setTextAndPlaceCursorAtEnd(options.getOrElse(selectedValue ?: -1) { "" })
    }

    LaunchedEffect(expanded) {
        if (!expanded) {
            focusManager.clearFocus()
        }
    }

    val itemHeight = 48.dp
    val maxVisible = 8
    val flags = remember(options) { options.associateWith { Flag.fromDisplayName(it) } }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            state = textFieldState,
            readOnly = true,
            lineLimits = TextFieldLineLimits.SingleLine,
            label = { Text(label) },
            trailingIcon = {
                val description = if (expanded) {
                    stringResource(SharedRes.strings.expanded)
                } else {
                    stringResource(SharedRes.strings.collapsed)
                }
                Box(Modifier.semantics { contentDescription = description }) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            LazyColumn(
                modifier = Modifier.heightIn(max = itemHeight * min(options.size, maxVisible)),
            ) {
                items(options.size) { index ->
                    val option = options[index]
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            textFieldState.setTextAndPlaceCursorAtEnd(option)
                            expanded = false
                            onSelectionChanged(options.indexOf(option))
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        trailingIcon = {
                            flags[option]?.let { flag ->
                                if (label != stringResource(SharedRes.strings.region)) {
                                    // Flag icon is decorative when label is not region
                                    Image(
                                        painter = painterResource(flag.toDrawable()),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clearAndSetSemantics {}
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