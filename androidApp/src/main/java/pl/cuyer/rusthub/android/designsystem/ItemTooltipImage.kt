package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemTooltipImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    text: String? = null,
    tooltipText: String? = null,
    addBlueprint: Boolean = false,
    overlayText: String? = null,
    size: Int = 48
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val imageContent: @Composable () -> Unit = {
            Box(
                modifier = Modifier.size(size.dp),
                contentAlignment = Alignment.Center
            ) {
                var success by rememberSaveable {
                    mutableStateOf(false)
                }
                if (addBlueprint && success) {
                    Image(
                        painter = painterResource(SharedRes.images.ic_blueprint.drawableResId),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize()
                    )
                }

                SubcomposeAsyncImage(
                    modifier = Modifier.matchParentSize(),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    loading = {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .shimmer()
                        )
                    },
                    error = {
                        Image(
                            painter = painterResource(getImageByFileName("ic_fallback").drawableResId),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                    },
                    onSuccess = {
                        success = true
                    },
                    onError = {
                        success = false
                    },
                    onLoading = {
                        success = false
                    }
                )

                overlayText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            )
                            .padding(horizontal = 2.dp)
                    )
                }
            }
        }

        if (tooltipText != null) {
            val tooltipState = rememberTooltipState()
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    positioning = TooltipAnchorPosition.Above
                ),
                tooltip = {
                    PlainTooltip(
                        caretShape = TooltipDefaults.caretShape()
                    ) {
                        Text(
                            modifier = Modifier.padding(spacing.small),
                            text = tooltipText
                        )
                    }
                },
                state = tooltipState
            ) {
                imageContent()
            }
        } else {
            imageContent()
        }

        text?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}