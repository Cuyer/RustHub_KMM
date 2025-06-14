package pl.cuyer.rusthub.android.designsystem

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.android.model.Label
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ServerListItem(
    modifier: Modifier = Modifier,
    serverName: String,
    @DrawableRes flag: Int,
    labels: List<Label>,
    details: Map<String, String>
) {
    ElevatedCard(
        shape = RectangleShape,
        modifier = modifier
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = spacing.xmedium,
                    vertical = spacing.xxmedium
                ),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f),
                ) {
                    Text(
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        maxLines = 2,
                        text = serverName
                    )
                }
                Image(
                    painter = painterResource(flag),
                    contentDescription = "Server flag",
                    modifier = Modifier
                        .size(24.dp),
                    contentScale = ContentScale.Fit
                )
            }
            LabelRow(
                labels = labels
            )
            DetailsRow(
                details = details
            )
        }
    }
}

@Preview(
    showSystemUi = false, showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ServerListItemPreview() {
    RustHubTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ServerListItem(
                modifier = Modifier
                    .padding(horizontal = spacing.small),
                serverName = "Rustoria [EU/UK] WIPE WIPE WIPE WIPE",
                labels = listOf(
                    Label(
                        text = "Monthly"
                    ),
                    Label(
                        text = "Weekly"
                    )
                ),
                flag = getImageByFileName("gb").drawableResId,
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