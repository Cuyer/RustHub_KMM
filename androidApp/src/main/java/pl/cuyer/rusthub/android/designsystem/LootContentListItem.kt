package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.LootAmount
import pl.cuyer.rusthub.domain.model.LootContent

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LootContentListItem(
    modifier: Modifier = Modifier,
    content: LootContent
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.xmedium, vertical = spacing.xxmedium),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val stackText = content.stack?.let { formatRange(it) }
            content.image?.let {
                ItemTooltipImage(
                    imageUrl = it,
                    text = stackText,
                    tooltipText = content.spawn
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)) {
                Text(
                    text = content.spawn.orEmpty(),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                    content.chance?.let { chance ->
                        val percent = if (chance <= 1) chance * 100 else chance
                        val label = stringResource(SharedRes.strings.chance)
                        Text(
                            text = "$label ${"%.1f".format(percent)}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    content.amount?.let { amount ->
                        val amountText = formatRange(amount)
                        amountText?.let { value ->
                            val label = stringResource(SharedRes.strings.amount)
                            Text(
                                text = "$label $value",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatRange(range: LootAmount): String? {
    val min = range.min
    val max = range.max
    return when {
        min != null && max != null && min != max -> "x${min}-${max}"
        min != null -> "x$min"
        max != null -> "x$max"
        else -> null
    }
}
