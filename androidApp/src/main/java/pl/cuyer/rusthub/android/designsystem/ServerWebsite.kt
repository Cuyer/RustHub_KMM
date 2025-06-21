package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import pl.cuyer.rusthub.android.theme.Spacing

@Composable
fun ServerWebsite(
    website: String,
    modifier: Modifier = Modifier,
    spacing: Spacing,
    label: String = "Website",
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    urlColor: Color = MaterialTheme.colorScheme.primary
) {
    if (website.isNotBlank()) {
        val uriHandler = LocalUriHandler.current

        Row(modifier = modifier.padding(spacing.medium)) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.bodyLarge,
                color = labelColor
            )
            Text(
                text = website,
                style = MaterialTheme.typography.bodyLarge,
                color = urlColor,
                modifier = Modifier.clickable {
                    uriHandler.openUri(website)
                }
            )
        }
    }
}