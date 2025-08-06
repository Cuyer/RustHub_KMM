package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.ItemTooltipImage
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.domain.model.Mining

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MonumentMiningPage(
    mining: Mining,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item(key = "mining", contentType = "mining") {
            ElevatedCard(
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(horizontal = spacing.xmedium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.xmedium, vertical = spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    mining.item?.let { item ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ItemTooltipImage(
                                imageUrl = item.image ?: "",
                                text = item.amount?.let { amount ->
                                    stringResource(SharedRes.strings.multiplier_format, amount)
                                }
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)) {
                                Text(
                                    text = item.name.orEmpty(),
                                    style = MaterialTheme.typography.titleLargeEmphasized
                                )
                                mining.timePerFuelSeconds?.let { secs ->
                                    Text(
                                        text = stringResource(
                                            SharedRes.strings.time_per_fuel,
                                            secs
                                        ),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    mining.productionItems?.takeIf { it.isNotEmpty() }?.let { outputs ->
                        Text(
                            text = stringResource(SharedRes.strings.output),
                            style = MaterialTheme.typography.titleMedium
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                            verticalArrangement = Arrangement.spacedBy(spacing.small),
                            itemVerticalAlignment = Alignment.CenterVertically
                        ) {
                            outputs.forEach { output ->
                                ItemTooltipImage(
                                    imageUrl = output.image ?: "",
                                    text = output.amount?.let { amount ->
                                        stringResource(
                                            SharedRes.strings.multiplier_format,
                                            amount
                                        )
                                    },
                                    tooltipText = output.name
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

