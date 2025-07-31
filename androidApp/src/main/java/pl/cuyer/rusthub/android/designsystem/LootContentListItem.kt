package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.theme.RustHubTheme
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
            content.image?.let { imageUrl ->
                val stackText = lootAmountText(content.stack)
                ItemTooltipImage(
                    imageUrl = imageUrl,
                    tooltipText = content.spawn,
                    overlayText = stackText
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)) {
                Text(
                    text = content.spawn.orEmpty(),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                    content.chance?.let {
                        val percent = if (it <= 1) it * 100 else it
                        val label = stringResource(SharedRes.strings.chance)
                        Text(
                            text = "$label ${"%.1f".format(percent)}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    lootAmountText(content.amount)?.let { value ->
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

private fun lootAmountText(amount: LootAmount?): String? {
    val min = amount?.min
    val max = amount?.max
    return when {
        min != null && max != null && min != max -> "x${min}-${max}"
        min != null -> "x$min"
        max != null -> "x$max"
        else -> null
    }
}

@Preview
@Composable
private fun LootContentListItemPreview() {
    RustHubTheme {
        LootContentListItem(
            content = LootContent(
                spawn = "Item",
                image = "",
                stack = LootAmount(1, 2),
                chance = 0.5,
                amount = LootAmount(1, 1)
            )
        )
    }
}
