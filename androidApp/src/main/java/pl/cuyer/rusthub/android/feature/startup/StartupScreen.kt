package pl.cuyer.rusthub.android.feature.startup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import pl.cuyer.rusthub.android.designsystem.AppTextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StartupScreen(
    showSkip: () -> Boolean,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.CenterVertically)
    ) {
        CircularWavyProgressIndicator()
        Text(
            color = MaterialTheme.colorScheme.onSurface,
            text = stringResource(SharedRes.strings.synchronizing_data),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (showSkip()) {
            AppTextButton(
                onClick = onSkip
            ) {
                Text(stringResource(SharedRes.strings.skip))
            }
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                text = stringResource(SharedRes.strings.fetching_in_background),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
