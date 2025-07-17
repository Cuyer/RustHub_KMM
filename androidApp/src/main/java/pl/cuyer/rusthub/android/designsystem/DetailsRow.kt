package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.domain.model.Theme

@Composable
fun DetailsRow(details: Map<String, String>) {
    FlowColumn(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        maxItemsInEachColumn = 3,
        maxLines = 2,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        details.forEach {
            Row {
                Text(
                    style = MaterialTheme.typography.bodyLarge.copy(
                    ),
                    text = it.key + ": "
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    text = it.value
                )

            }
        }
    }
}

@Preview
@Composable
private fun DetailsRowPreview() {
    RustHubTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DetailsRow(
                details = mapOf(
                    "Wipe" to "4hrs ago",
                    "Rating" to "72%",
                    "Cycle" to "6.8 days",
                    "Players" to "132/150",
                    "Map" to "Custom",
                    "Modded" to "Yes"
                )
            )
        }
    }
}