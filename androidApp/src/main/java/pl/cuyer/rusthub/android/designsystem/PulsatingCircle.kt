package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@Composable
fun PulsatingCircle(
    modifier: Modifier = Modifier,
    isOnline: Boolean
) {
    val pulse = rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val coreColor = remember(isOnline) {
        if (isOnline) Color(0xFF008939) else Color(0xFFEA1B0C)
    }
    val cd =  if (isOnline) {
        stringResource(SharedRes.strings.online)
    } else {
        stringResource(SharedRes.strings.offline)
    }
    Canvas(
        modifier = modifier
            .size(12.dp)
            .semantics {
                contentDescription =cd
            }
    ) {
        val center = this.center
        val radius = size.minDimension / 2f

        drawCircle(
            color = coreColor.copy(alpha = 0.3f),
            radius = radius * pulse.value,
            center = center
        )

        drawCircle(
            color = coreColor,
            radius = radius * 0.6f,
            center = center
        )
    }
}

@Composable
@Preview
private fun PulsatingCirclePreview(modifier: Modifier = Modifier) {

}