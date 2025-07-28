package pl.cuyer.rusthub.android.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.matchParentSize
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
import androidx.compose.foundation.Image
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import pl.cuyer.rusthub.android.theme.RustHubTheme
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.RustItem

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ItemListItem(
    modifier: Modifier = Modifier,
    item: RustItem,
    onClick: (Long) -> Unit
) {
    ElevatedCard(
        onClick = {
            onClick(item.id ?: 0L)
        },
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.xmedium, vertical = spacing.xxmedium),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier.size(48.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.iconUrl ?: item.image)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                loading = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .shimmer()
                    )
                },
                error = {
                    Image(
                        painter = painterResource(getImageByFileName("ic_error").drawableResId),
                        contentDescription = item.name,
                        modifier = Modifier.matchParentSize()
                    )
                }
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xxsmall)) {
                Text(
                    text = item.name.orEmpty(),
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
                item.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ItemListItemPreview() {
    RustHubTheme {
        ItemListItem(
            onClick = {},
            item = RustItem(
                name = "Assault Rifle",
                description = "High damage automatic rifle",
                iconUrl = ""
            )
        )
    }
}
