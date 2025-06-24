package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.android.theme.RustHubTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
    isLoading: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        shape = RectangleShape,
        colors = colors
    ) {
        AnimatedContent(
            contentAlignment = Alignment.Center,
            targetState = isLoading,
            transitionSpec = { defaultFadeTransition() }
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                content()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    isLoading: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        shape = RectangleShape,
        colors = colors
    ) {
        AnimatedContent(
            contentAlignment = Alignment.Center,
            targetState = isLoading,
            transitionSpec = { defaultFadeTransition() }
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                content()
            }
        }
    }
}

@Preview(name = "AppButton — Idle & Loading", showBackground = true, widthDp = 200)
@Composable
@OptIn(ExperimentalAnimationApi::class)
fun PreviewAppButton() {
    RustHubTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppButton(
                onClick = { /* noop */ },
                isLoading = false
            ) {
                Text("Submit")
            }
            AppButton(
                onClick = { /* noop */ },
                isLoading = true
            ) {
                Text("Submit")
            }
        }
    }
}

@Preview(name = "AppOutlinedButton — Idle & Loading", showBackground = true, widthDp = 200)
@Composable
@OptIn(ExperimentalAnimationApi::class)
fun PreviewAppOutlinedButton() {
    RustHubTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppOutlinedButton(
                onClick = { /* noop */ },
                isLoading = false
            ) {
                Text("Delete")
            }
            AppOutlinedButton(
                onClick = { /* noop */ },
                isLoading = true
            ) {
                Text("Delete")
            }
        }
    }
}

