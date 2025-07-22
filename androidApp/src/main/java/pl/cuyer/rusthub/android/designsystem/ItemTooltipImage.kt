package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
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

                AsyncImage(
                    modifier = Modifier.matchParentSize(),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(getImageByFileName("ic_placeholder").drawableResId),
                    error = painterResource(getImageByFileName("ic_error").drawableResId),
                    contentDescription = null,
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
            }
        }

        if (tooltipText != null) {
            val tooltipState = rememberTooltipState()
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above,
                    spacing.small
                ),
                tooltip = {
                    PlainTooltip(caretShape = TooltipDefaults.caretShape()) {
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