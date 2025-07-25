package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import pl.cuyer.rusthub.android.theme.Spacing
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.util.composeUtil.stringResource

@Composable
fun ServerWebsite(
    website: String,
    modifier: Modifier = Modifier,
    spacing: Spacing,
    label: String = stringResource(SharedRes.strings.website),
    alias: String? = null,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    urlColor: Color = MaterialTheme.colorScheme.primary
) {
    if (website.isNotBlank()) {
        val annotatedText = buildAnnotatedString {
            // Label
            withStyle(style = SpanStyle(color = labelColor)) {
                append("$label: ")
            }

            // Link
            val displayText = alias ?: website
            val start = length
            append(displayText)
            addStringAnnotation(
                tag = "URL",
                annotation = website,
                start = start,
                end = start + displayText.length
            )
            addStyle(
                style = SpanStyle(color = urlColor),
                start = start,
                end = start + displayText.length
            )
            addLink(
                LinkAnnotation.Url(website),
                start = start,
                end = start + displayText.length
            )
            addStyle(
                style = SpanStyle(
                    color = urlColor
                ),
                start = start,
                end = length
            )
        }

        Text(
            text = annotatedText,
            modifier = modifier.padding(spacing.medium),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}