package pl.cuyer.rusthub.android.designsystem

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SwitchWithText(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Text(text)
    AppSwitch(isChecked = isChecked, onCheckedChange = onCheckedChange)
}

@Preview
@Composable
private fun SwitchWithTextPreview() {

}