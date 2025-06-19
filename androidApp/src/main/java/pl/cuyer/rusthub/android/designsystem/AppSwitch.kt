package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun AppSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    var checked by remember(isChecked) { mutableStateOf(isChecked) }
    Switch(
        modifier = Modifier.semantics { contentDescription = "Demo with icon" },
        checked = checked,
        onCheckedChange = {
            checked = it
            onCheckedChange(it)
        },
        thumbContent = {
            if (checked) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        }
    )
}