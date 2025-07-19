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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@Composable
fun AppSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    var checked by remember(isChecked) { mutableStateOf(isChecked) }
    val sd = if (checked) {
        stringResource(SharedRes.strings.enabled)
    } else {
        stringResource(SharedRes.strings.disabled)
    }

    Switch(
        modifier = Modifier.semantics {
            role = Role.Switch
            stateDescription = sd
        },
        checked = checked,
        onCheckedChange = {
            checked = it
            onCheckedChange(it)
        },
        thumbContent = {
            if (checked) {
                // Decorative check mark shown inside the switch
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = SwitchDefaults.colors().checkedTrackColor
                )
            }
        }
    )
}
