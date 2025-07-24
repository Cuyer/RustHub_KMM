package pl.cuyer.rusthub.android.util

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey


@DslMarker
annotation class PagingDSL

@PagingDSL
class PagingHandlerScope<T : Any>(
    val items: LazyPagingItems<T>,
    private val handledState: MutableState<Boolean>
) {
    private inline val handled: Boolean
        get() = handledState.value
    private fun handle() { handledState.value = true }

    @Composable
    fun onEmpty(body: @Composable () -> Unit) {
        if (handled) return
        val loadState = items.loadState
        if (
            loadState.refresh is LoadState.NotLoading &&
            items.itemCount == 0 &&
            loadState.append.endOfPaginationReached
        ) {
            handle()
            body()
        }
    }

    @Composable
    fun onRefresh(body: @Composable () -> Unit) {
        if (handled) return
        val loadState = items.loadState
        if (loadState.refresh is LoadState.Loading) {
            handle()
            body()
        }
    }

    @Composable
    fun onSuccess(body: @Composable () -> Unit) {
        if (!handled) {
            handle()
            body()
        }
    }

    @Composable
    fun onError(body: @Composable (Throwable) -> Unit) {
        if (handled) return
        val loadState = items.loadState
        if (loadState.refresh is LoadState.Error) {
            val error = (loadState.refresh as LoadState.Error).error
            handle()
            body(error)
        } else this
    }

    @LazyScopeMarker
    fun LazyListScope.onAppendItem(body: @Composable LazyItemScope.() -> Unit) {
        val loadState = items.loadState
        if (loadState.append == LoadState.Loading) {
            item { body(this) }
        }
    }

    @LazyScopeMarker
    fun LazyListScope.onLastItem(body: @Composable LazyItemScope.() -> Unit) {
        val loadState = items.loadState
        if (loadState.append.endOfPaginationReached) item { body(this) }
    }

    @LazyScopeMarker
    fun LazyListScope.onPagingItems(
        key: ((T) -> Any)?,
        body: @Composable LazyItemScope.(T) -> Unit
    ) {
        items(
            count = items.itemCount,
            key = items.itemKey(key),
        ) { index ->
            val item = items[index]
            item?.let {
                body(it)
            }
        }
    }
}

@Composable
fun <T : Any> HandlePagingItems(
    items: () -> LazyPagingItems<T>,
    content: @Composable PagingHandlerScope<T>.() -> Unit
) {
    val lazyItems = items()
    val handled = remember(lazyItems.loadState.refresh, lazyItems.itemCount) {
        mutableStateOf(false)
    }
    val scope = remember(lazyItems) { PagingHandlerScope(lazyItems, handled) }
    scope.content()
}