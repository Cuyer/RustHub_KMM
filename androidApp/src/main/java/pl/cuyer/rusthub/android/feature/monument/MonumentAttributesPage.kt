package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.DetailsRow
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.domain.model.MonumentAttributes
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.util.StringProvider

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MonumentAttributesPage(
    attributes: MonumentAttributes,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item(key = "attributes", contentType = "attributes") {
            MonumentAttributesCard(
                attributes = attributes,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(horizontal = spacing.xmedium)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MonumentAttributesCard(
    attributes: MonumentAttributes,
    modifier: Modifier = Modifier
) {
    val stringProvider = koinInject<StringProvider>()
    ElevatedCard(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = spacing.xmedium,
                vertical = spacing.medium
            )
        ) {
            DetailsRow(
                maxItemsInColumn = 5,
                details = {
                    buildMap {
                        attributes.type?.let { type ->
                            put(
                                stringProvider.get(SharedRes.strings.type),
                                type.displayName(stringProvider)
                            )
                        }
                        attributes.isSafezone?.let { safe ->
                            put(
                                stringProvider.get(SharedRes.strings.safe_zone),
                                stringProvider.get(if (safe) SharedRes.strings.yes else SharedRes.strings.no)
                            )
                        }
                        attributes.hasTunnelEntrance?.let { tunnel ->
                            put(
                                stringProvider.get(SharedRes.strings.tunnel_entrance),
                                stringProvider.get(if (tunnel) SharedRes.strings.yes else SharedRes.strings.no)
                            )
                        }
                        attributes.hasChinookDropZone?.let { chinook ->
                            put(
                                stringProvider.get(SharedRes.strings.chinook_drop_zone),
                                stringProvider.get(if (chinook) SharedRes.strings.yes else SharedRes.strings.no)
                            )
                        }
                        attributes.allowsPatrolHeliCrash?.let { heli ->
                            put(
                                stringProvider.get(SharedRes.strings.patrol_heli_crash),
                                stringProvider.get(if (heli) SharedRes.strings.yes else SharedRes.strings.no)
                            )
                        }
                        attributes.recyclers?.let { put(stringProvider.get(SharedRes.strings.recyclers), it.toString()) }
                        attributes.barrels?.let { put(stringProvider.get(SharedRes.strings.barrels), it.toString()) }
                        attributes.crates?.let { put(stringProvider.get(SharedRes.strings.crates), it.toString()) }
                        attributes.scientists?.let { put(stringProvider.get(SharedRes.strings.scientists), it.toString()) }
                        attributes.medianRadiationLevel?.let {
                            put(stringProvider.get(SharedRes.strings.median_radiation_level), it.toString())
                        }
                        attributes.maxRadiationLevel?.let {
                            put(stringProvider.get(SharedRes.strings.max_radiation_level), it.toString())
                        }
                        attributes.hasRadiation?.let { rad ->
                            put(
                                stringProvider.get(SharedRes.strings.radiation),
                                stringProvider.get(if (rad) SharedRes.strings.yes else SharedRes.strings.no)
                            )
                        }
                    }
                }
            )
        }
    }
}

