package pl.cuyer.rusthub.android.util

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey

@DslMarker
annotation class PagingDSL

@PagingDSL
class PagingHandlerScope<T : Any>(
    val items: LazyPagingItems<T>,
    private val loadState: CombinedLoadStates
) {
    @LazyScopeMarker
    fun LazyListScope.onAppendItem(body: @Composable LazyItemScope.() -> Unit) {
        if (loadState.append == LoadState.Loading) {
            item { body(this) }
        }
    }

    @LazyScopeMarker
    fun LazyListScope.onLastItem(body: @Composable LazyItemScope.() -> Unit) {
        if (loadState.append.endOfPaginationReached) item { body(this) }
    }

    @LazyScopeMarker
    fun LazyListScope.onPagingItems(
        key: ((T) -> Any)? = null,
        body: @Composable LazyItemScope.(T) -> Unit
    ) {
        items(
            count = items.itemCount,
            key = items.itemKey(key)
        ) { index ->
            items[index]?.let { body(it) }
        }
    }
}

@Composable
fun <T : Any> HandlePagingItems(
    items: () -> LazyPagingItems<T>,
    onRefresh: @Composable () -> Unit = {},
    onEmpty: @Composable () -> Unit = {},
    onError: @Composable (Throwable) -> Unit = {},
    onSuccess: @Composable PagingHandlerScope<T>.() -> Unit
) {
    val pagingItems = items()
    val loadState = pagingItems.loadState

    when {
        loadState.refresh is LoadState.Loading -> onRefresh()
        loadState.refresh is LoadState.Error ->
            onError((loadState.refresh as LoadState.Error).error)
        pagingItems.itemCount == 0 && loadState.append.endOfPaginationReached -> onEmpty()
        else -> {
            val scope = remember(pagingItems, loadState) {
                PagingHandlerScope(pagingItems, loadState)
            }
            scope.onSuccess()
        }
    }
}
