package pl.cuyer.rusthub.android.designsystem

import android.content.res.Configuration
import pl.cuyer.rusthub.domain.model.Flag
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.android.model.Label
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import pl.cuyer.rusthub.domain.model.Flag.Companion.toDrawable
import pl.cuyer.rusthub.domain.model.displayName

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ServerListItem(
    modifier: Modifier = Modifier,
    serverName: String,
    isOnline: Boolean,
    flag: Flag?,
    labels: List<Label>,
    details: Map<String, String>
) {
    val statusText = if (isOnline) {
        stringResource(SharedRes.strings.online)
    } else {
        stringResource(SharedRes.strings.offline)
    }

    val playersKey = stringResource(SharedRes.strings.players)
    val playersInfo = details[playersKey]?.let { "$playersKey $it" }
    val semanticsDescription = buildString {
        append(serverName)
        append(", ")
        append(statusText)
        playersInfo?.let {
            append(", ")
            append(it)
        }
    }
    ElevatedCard(
        shape = RectangleShape,
        modifier = modifier
            .wrapContentHeight()
            .semantics {
                role = Role.Button
                contentDescription = semanticsDescription
            }
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
                    .height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.80f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        maxLines = 2,
                        text = serverName
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.End
                ) {
                    PulsatingCircle(
                        modifier = Modifier,
                        isOnline = isOnline
                    )
                    Image(
                        modifier = modifier
                            .size(24.dp),
                        painter = painterResource(flag.toDrawable()),
                        contentDescription = flag?.displayName ?: stringResource(SharedRes.strings.server_flag),
                        contentScale = ContentScale.Fit
                    )
                }
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


@Composable
fun ServerListItemShimmer(modifier: Modifier = Modifier) {
    ElevatedCard(
        shape = RectangleShape,
        modifier = modifier.wrapContentHeight()
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(24.dp)
                            .shimmer()
                            .clearAndSetSemantics {}
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RectangleShape)
                            .shimmer()
                            .clearAndSetSemantics {}
                    )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = spacing.small,
                    alignment = Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(60.dp)
                            .clip(RectangleShape)
                            .shimmer()
                            .clearAndSetSemantics {}
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.xsmall)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmer()
                            .clearAndSetSemantics {}
                    )
                }
            }
        }
    }
}

@Preview(
    showSystemUi = false, showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ServerListItemPreview() {
    RustHubTheme() {
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
                flag = Flag.GB,
                isOnline = true,
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
