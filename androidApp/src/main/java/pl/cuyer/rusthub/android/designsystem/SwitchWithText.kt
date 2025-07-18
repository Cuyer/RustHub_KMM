package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.cuyer.rusthub.android.theme.spacing

@Composable
fun SwitchWithText(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Text(text)
    AppSwitch(isChecked = isChecked, onCheckedChange = onCheckedChange)
}

@Composable
fun SwitchWithTextHorizontal(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = spacing.medium)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text)
        AppSwitch(isChecked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Preview
@Composable
private fun SwitchWithTextPreview() {

}