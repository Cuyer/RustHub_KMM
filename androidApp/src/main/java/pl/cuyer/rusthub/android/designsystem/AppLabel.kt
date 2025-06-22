package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppLabel(
    modifier: Modifier = Modifier,
    text: String
) {
    ElevatedCard(
        modifier = modifier
            .height(IntrinsicSize.Max),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
        ) {
            VerticalDivider(
                modifier = Modifier,
                thickness = spacing.xsmall,
                color = Color(0xFFAB2915)
            )
            Text(
                modifier = Modifier
                    .padding(vertical = spacing.small, horizontal = spacing.medium),
                style = MaterialTheme.typography.labelLargeEmphasized,
                text = text
            )
        }
    }
}

@Composable
@Preview
private fun LabelPreview() {
    RustHubTheme {
        AppLabel(
            text = "Monthly"
        )
    }
}