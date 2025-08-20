package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.ui.unit.min
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.UsableEntity

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MonumentUsableEntitiesPage(
    entities: List<UsableEntity>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        items(entities) { entity ->
            UsableEntityCard(
                entity = entity,
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
private fun UsableEntityCard(
    entity: UsableEntity,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = spacing.xmedium,
                    vertical = spacing.xxmedium
                ),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            entity.image?.let {
                SubcomposeAsyncImage(
                    modifier = Modifier.size(48.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(it)
                        .crossfade(true)
                        .build(),
                    contentDescription = entity.name,
                    loading = {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .shimmer()
                        )
                    },
                    error = {
                        Image(
                            painter = painterResource(
                                getImageByFileName("ic_fallback").drawableResId
                            ),
                            contentDescription = entity.name,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                )
            }

            Text(
                modifier = Modifier.weight(1f),
                text = entity.name.orEmpty(),
                style = MaterialTheme.typography.titleLargeEmphasized
            )
            entity.amount?.let { amount ->
                Text(
                    text = "x$amount",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

