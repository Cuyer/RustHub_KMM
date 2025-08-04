package pl.cuyer.rusthub.android.util

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@DslMarker
annotation class PagingDSL

@PagingDSL
@Immutable
class PagingHandlerScope<T : Any>(
    val items: LazyPagingItems<T>,
    private val loadState: CombinedLoadStates
) {

    @LazyScopeMarker
    fun LazyListScope.onAppendItem(
        key: Any = "append",
        contentType: Any = "append",
        body: @Composable LazyItemScope.() -> Unit
    ) {
        if (loadState.append == LoadState.Loading) {
            item(key = key, contentType = contentType) { body(this) }
        }
    }

    @LazyScopeMarker
    fun LazyListScope.onLastItem(body: @Composable LazyItemScope.() -> Unit) {
        if (loadState.append.endOfPaginationReached) item { body(this) }
    }

    @LazyScopeMarker
    fun LazyListScope.onPagingItemsIndexed(
        key: ((Int, T) -> Any)? = null,
        contentType: ((Int, T) -> Any)? = null,
        body: @Composable LazyItemScope.(Int, T) -> Unit
    ) {
        items(
            count = items.itemCount,
            key = key?.let { keyLambda ->
                { index: Int ->
                    items.peek(index)?.let { item -> keyLambda(index, item) } ?: index
                }
            },
            contentType = {
                contentType?.let { typeLambda ->
                    { index: Int ->
                        items.peek(index)?.let { item -> typeLambda(index, item) } ?: Unit
                    }
                }
            }
        ) { index ->
            items[index]?.let { body(index, it) }
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
        pagingItems.itemCount == 0 &&
                loadState.refresh is LoadState.NotLoading &&
                loadState.append.endOfPaginationReached -> onEmpty()
        else -> {
            val scope = remember(pagingItems, loadState) {
                PagingHandlerScope(pagingItems, loadState)
            }
            scope.onSuccess()
        }
    }
}
