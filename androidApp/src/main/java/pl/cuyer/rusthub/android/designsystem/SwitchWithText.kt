package pl.cuyer.rusthub.android.designsystem

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SwitchWithText(text: String, isChecked: Boolean) {
    Text(text)
    AppSwitch(isChecked = isChecked)
}

@Preview
@Composable
private fun SwitchWithTextPreview() {

}