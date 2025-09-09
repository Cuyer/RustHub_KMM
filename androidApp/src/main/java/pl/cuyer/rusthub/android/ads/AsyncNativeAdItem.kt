package pl.cuyer.rusthub.android.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import pl.cuyer.rusthub.android.designsystem.shimmer
import pl.cuyer.rusthub.presentation.features.ads.AdAction
import pl.cuyer.rusthub.presentation.features.ads.NativeAdViewModel

@Composable
fun AsyncNativeAdItem(
    adId: String,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    mediaHeight: Dp = 180.dp
) {
    val viewModel = koinViewModel<NativeAdViewModel>()
    val state by viewModel.state.collectAsState()
    val loadedAd = state.ads[adId]
    val ad = remember(loadedAd) { loadedAd }

    LaunchedEffect(adId) { viewModel.onAction(AdAction.LoadAd(adId)) }

    LaunchedEffect(listState, ad) {
        if (ad == null) return@LaunchedEffect
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.any { it.key == adId } }
            .collectLatest { visible ->
                val controller = ad.mediaContent?.videoController
                if (visible) controller?.play() else controller?.pause()
            }
    }

    DisposableEffect(ad) {
        onDispose {
            ad?.mediaContent?.videoController?.pause()
            ad?.destroy()
            viewModel.clear()
        }
    }

    if (ad == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(mediaHeight)
                .shimmer()
        )
    } else {
        NativeAdCard(
            modifier = modifier,
            ad = ad,
            mediaHeight = mediaHeight
        )
    }
}

