package pl.cuyer.rusthub.android.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun rememberShouldDisplayAds(
    showAds: Boolean,
    pagingItems: LazyPagingItems<*>
): Boolean {
    var shouldDisplayAds by remember(pagingItems) { mutableStateOf(false) }

    val loadState = pagingItems.loadState

    val refreshLoading = loadState.refresh is LoadState.Loading ||
        loadState.mediator?.refresh is LoadState.Loading
    val appendLoading = loadState.append is LoadState.Loading ||
        loadState.mediator?.append is LoadState.Loading
    val prependLoading = loadState.prepend is LoadState.Loading ||
        loadState.mediator?.prepend is LoadState.Loading

    val isBlockingInitialLoad = refreshLoading || (!shouldDisplayAds && (appendLoading || prependLoading))

    LaunchedEffect(showAds) {
        if (!showAds && shouldDisplayAds) {
            shouldDisplayAds = false
        }
    }

    LaunchedEffect(showAds, isBlockingInitialLoad) {
        when {
            showAds && !isBlockingInitialLoad && !shouldDisplayAds -> shouldDisplayAds = true
            isBlockingInitialLoad && shouldDisplayAds -> shouldDisplayAds = false
        }
    }

    return shouldDisplayAds
}
