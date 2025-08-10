package pl.cuyer.rusthub.android.feature.monument

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.android.designsystem.MapDialog
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.android.theme.spacing
import pl.cuyer.rusthub.android.util.composeUtil.stringResource
import pl.cuyer.rusthub.common.getImageByFileName

@Composable
fun MonumentMapPage(
    mapUrls: List<String>,
    modifier: Modifier = Modifier
) {
    var showImage by remember { mutableStateOf<Any?>(null) }
    val uriHandler = LocalUriHandler.current

    showImage?.let { model ->
        MapDialog(imageModel = model) { showImage = null }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        item(key = "legend", contentType = "legend") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(horizontal = spacing.xmedium)
            ) {
                Text(
                    text = stringResource(SharedRes.strings.map_legend),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )
                val legendRes = getImageByFileName("il_legend").drawableResId
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { showImage = legendRes },
                    painter = painterResource(id = legendRes),
                    contentDescription = stringResource(SharedRes.strings.map_legend)
                )
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .padding(horizontal = spacing.xmedium)
            ) {
                Text(
                    text = stringResource(SharedRes.strings.maps),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )
            }
        }
        items(mapUrls, key = { it }) { mapUrl ->
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .animateItem()
                    .padding(horizontal = spacing.xmedium)
                    .clickable { showImage = mapUrl },
                model = mapUrl,
                contentDescription = stringResource(SharedRes.strings.rust_map_image),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .shimmer()
                    )
                },
                error = {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        painter = painterResource(id = getImageByFileName("il_not_found").drawableResId),
                        contentDescription = stringResource(SharedRes.strings.error_not_found)
                    )
                }
            )
        }
        item(key = "credit", contentType = "credit") {
            Text(
                text = stringResource(SharedRes.strings.map_creator_credit),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .animateItem()
                    .padding(horizontal = spacing.xmedium)
                    .clickable {
                        uriHandler.openUri("https://steamcommunity.com/sharedfiles/filedetails/?id=2428742835")
                    }
            )
        }
    }
}
