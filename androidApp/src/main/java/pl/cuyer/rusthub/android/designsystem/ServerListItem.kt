package pl.cuyer.rusthub.android.designsystem

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
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
    isOnline: Boolean,
    @DrawableRes flag: Int,
    labels: List<Label>,
    details: Map<String, String>
) {
    ElevatedCard(
        shape = RectangleShape,
        modifier = modifier
            .wrapContentHeight()
    ) {
        Box {
            val infiniteTransition = rememberInfiniteTransition()
            val pulse = infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(spacing.small)
                    .size(spacing.small)
                    .graphicsLayer {
                        scaleX = pulse.value
                        scaleY = pulse.value
                    }
            ) {
                drawCircle(
                    color = if (isOnline) Color.Green else Color.Red,
                    radius = size.minDimension / 2f
                )
            }
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
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RectangleShape)
                        .shimmer()
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