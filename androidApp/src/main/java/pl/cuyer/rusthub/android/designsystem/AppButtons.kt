package pl.cuyer.rusthub.android.designsystem

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
    isLoading: Boolean = false,
    loadingIndicator: @Composable () -> Unit = {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp
        )
    },
    content: @Composable () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.extraSmall,
        colors = colors
    ) {
        Crossfade(
            modifier = Modifier.fillMaxSize(),
            targetState = isLoading,
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy
            )
        ) { loading ->
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (loading) {
                    loadingIndicator()
                } else {
                    content()
                }
            }
        }
    }
}

@Composable
fun AppOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    isLoading: Boolean = false,
    content: @Composable () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.extraSmall,
        colors = colors
    ) {
        Crossfade(
            modifier = Modifier.fillMaxSize(),
            targetState = isLoading,
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy
            )
        ) { loading ->
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
}

@Composable
fun AppTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    isLoading: Boolean = false,
    content: @Composable () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.extraSmall,
        colors = colors
    ) {
        Crossfade(
            modifier = Modifier.fillMaxSize(),
            targetState = isLoading,
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy
            )
        ) { loading ->
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
}


@Preview(name = "AppButton — Idle & Loading", showBackground = true, widthDp = 200)
@Composable
fun PreviewAppButton() {
    RustHubTheme() {
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
                Text(stringResource(SharedRes.strings.submit))
            }
            AppButton(
                onClick = { /* noop */ },
                isLoading = true
            ) {
                Text(stringResource(SharedRes.strings.submit))
            }
        }
    }
}

@Preview(name = "AppOutlinedButton — Idle & Loading", showBackground = true, widthDp = 200)
@Composable
fun PreviewAppOutlinedButton() {
    RustHubTheme() {
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
                Text(stringResource(SharedRes.strings.delete))
            }
            AppOutlinedButton(
                onClick = { /* noop */ },
                isLoading = true
            ) {
                Text(stringResource(SharedRes.strings.delete))
            }
        }
    }
}

