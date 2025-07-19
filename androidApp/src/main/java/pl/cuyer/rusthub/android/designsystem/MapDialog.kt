package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import coil3.compose.SubcomposeAsyncImage
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@Composable
fun MapDialog(
    mapUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var zoom by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, gestureZoom, _ ->
                        // Clamp zoom
                        val oldZoom = zoom
                        val newZoom = (zoom * gestureZoom).coerceIn(1f, 5f)
                        // Offset update logic to keep centroid under the fingers
                        // This will make the zoom "feel natural"
                        offset = (offset + centroid / oldZoom) - (centroid / newZoom + pan / oldZoom)
                        zoom = newZoom
                    }
                }
        ) {

            val sd = stringResource(SharedRes.strings.map_gesture_hint)
            SubcomposeAsyncImage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .semantics {
                        stateDescription = sd
                    }
                    .graphicsLayer {
                        // Center image on screen, then apply pan/zoom
                        translationX = -offset.x * zoom
                        translationY = -offset.y * zoom
                        scaleX = zoom
                        scaleY = zoom
                        transformOrigin = TransformOrigin(0f, 0f)
                    },
                model = mapUrl,
                contentDescription = stringResource(SharedRes.strings.rust_map_image),
                loading = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .shimmer()
                    )
                },
                error = {
                    Image(
                        modifier = Modifier.matchParentSize(),
                        painter = painterResource(id = getImageByFileName("il_not_found").drawableResId),
                        contentDescription = stringResource(SharedRes.strings.error_not_found)
                    )
                }
            )
            val dismissLabel = stringResource(SharedRes.strings.dismiss)
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(spacing.medium)
                    .semantics {
                        onClick(label = dismissLabel) {
                            onDismiss()
                            true
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(SharedRes.strings.cancel),
                    tint = Color.White
                )
            }
        }
    }
}