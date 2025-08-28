package pl.cuyer.rusthub.android

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RustHubBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val primary = MaterialTheme.colorScheme.surface
    val secondary = MaterialTheme.colorScheme.surfaceContainer

    val gradient = remember(primary, secondary) {
        Brush.verticalGradient(colors = listOf(primary, secondary))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Surface(
            color = Color.Transparent,
            modifier = modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
                content()
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        )
    }
}