package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import pl.cuyer.rusthub.android.designsystem.ItemTooltipImage
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.MonumentPuzzle
import pl.cuyer.rusthub.domain.model.PuzzleRequirement
import pl.cuyer.rusthub.domain.model.SpawnGroup
import pl.cuyer.rusthub.domain.model.SpawnOption

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MonumentPuzzlesPage(
    puzzles: List<MonumentPuzzle>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        items(puzzles) { puzzle ->
            PuzzleCard(
                puzzle = puzzle,
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
private fun PuzzleCard(
    puzzle: MonumentPuzzle,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.xmedium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            puzzle.needToBring?.takeIf { it.isNotEmpty() }?.let { requirements ->
                PuzzleRequirementRow(
                    title = stringResource(SharedRes.strings.need_to_bring),
                    requirements = requirements
                )
            }
            puzzle.needToActivate?.takeIf { it.isNotEmpty() }?.let { requirements ->
                PuzzleRequirementRow(
                    title = stringResource(SharedRes.strings.need_to_activate),
                    requirements = requirements
                )
            }
            puzzle.entities?.map { it }.orEmpty().flatten().takeIf { it.isNotEmpty() }?.let { groups ->
                Text(
                    text = stringResource(SharedRes.strings.entities),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                    groups.forEach { group ->
                        PuzzleSpawnGroupRow(group)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PuzzleRequirementRow(
    title: String,
    requirements: List<PuzzleRequirement>
) {
    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLargeEmphasized
        )
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.medium)) {
            requirements.forEach { requirement ->
                requirement.image?.let { image ->
                    ItemTooltipImage(
                        imageUrl = image,
                        tooltipText = requirement.name,
                        overlayText = requirement.amount?.let { "x$it" }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PuzzleSpawnGroupRow(group: SpawnGroup) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(0.8f),
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

