package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.MonumentSpawns
import pl.cuyer.rusthub.domain.model.SpawnGroup
import pl.cuyer.rusthub.domain.model.SpawnOption

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MonumentSpawnsPage(
    spawns: MonumentSpawns,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        spawnSection(spawns.container, SharedRes.strings.container)
        spawnSection(spawns.collectable, SharedRes.strings.collectable)
        spawnSection(spawns.vehicle, SharedRes.strings.vehicle)
        spawnSection(spawns.scientist, SharedRes.strings.scientist)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.spawnSection(
    groups: List<SpawnGroup>?,
    title: dev.icerock.moko.resources.StringResource
) {
    groups?.takeIf { it.isNotEmpty() }?.let { list ->
        item {
            Text(
                modifier = Modifier.padding(horizontal = spacing.xmedium),
                text = stringResource(title),
                style = MaterialTheme.typography.titleLargeEmphasized
            )
        }
        items(list) { group ->
            SpawnGroupCard(
                group = group,
                modifier = Modifier
                    .padding(horizontal = spacing.xmedium)
                    .animateItem()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SpawnGroupCard(
    group: SpawnGroup,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = spacing.xmedium, vertical = spacing.xxmedium)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                group.options?.forEach { option ->
                    SpawnOptionRow(option)
                }
            }
            group.amount?.let { amount ->
                Text(
                    text = "x$amount",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SpawnOptionRow(option: SpawnOption) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.size(48.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(option.image)
                .crossfade(true)
                .build(),
            contentDescription = option.name,
            loading = {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .shimmer()
                )
            },
            error = {
                Image(
                    painter = painterResource(getImageByFileName("ic_fallback").drawableResId),
                    contentDescription = option.name,
                    modifier = Modifier.matchParentSize()
                )
            }
        )
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)) {
            Text(
                text = option.name.orEmpty(),
                style = MaterialTheme.typography.titleLargeEmphasized
            )
            option.chance?.let { chance ->
                val percent = if (chance <= 1) chance * 100 else chance
                val label = stringResource(SharedRes.strings.chance)
                Text(
                    text = "$label ${"%.1f".format(percent)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

