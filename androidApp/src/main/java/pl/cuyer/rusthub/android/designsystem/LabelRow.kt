package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.cuyer.rusthub.android.model.Label
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.Theme

@Composable
fun LabelRow(
    modifier: Modifier = Modifier,
    labels: () -> List<String>
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.spacedBy(space = spacing.small, alignment = Alignment.Start)
    ) {
        labels().forEach { label ->
            AppLabel(
                text = label.uppercase()
            )
        }
    }
}

@Preview
@Composable
private fun LabelRowPreview() {
    RustHubTheme() {
        LabelRow(
            labels = {
                listOf(
                    "Monthly",
                    "Weekly"
                )
            }
        )
    }
}