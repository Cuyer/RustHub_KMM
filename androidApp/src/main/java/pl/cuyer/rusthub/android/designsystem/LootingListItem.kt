package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.LootAmount
import pl.cuyer.rusthub.domain.model.Looting
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.getImageByFileName

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LootingListItem(
    modifier: Modifier = Modifier,
    looting: Looting
) {
    ElevatedCard(
        shape = RectangleShape,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.xmedium, vertical = spacing.xxmedium),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier.size(48.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(looting.image)
                    .crossfade(true)
                    .build(),
                contentDescription = looting.from,
                placeholder = painterResource(getImageByFileName("ic_placeholder").drawableResId),
                error = painterResource(getImageByFileName("ic_error").drawableResId),
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)) {
                Text(
                    text = looting.from.orEmpty(),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                    looting.chance?.let {
                        val percent = if (it <= 1) it * 100 else it
                        val label = stringResource(SharedRes.strings.chance)
                        Text(
                            text = "$label ${"%.1f".format(percent)}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    looting.amount?.let { amount ->
                        val amountText = when {
                            amount.min != null && amount.max != null && amount.min != amount.max -> "${amount.min}-${amount.max}"
                            amount.min != null -> amount.min.toString()
                            amount.max != null -> amount.max.toString()
                            else -> null
                        }
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

@Preview
@Composable
private fun LootingListItemPreview() {
    RustHubTheme {
        LootingListItem(
            looting = Looting(
                from = "Barrel",
                image = "",
                chance = 0.25,
                amount = LootAmount(1, 2)
            )
        )
    }
}
