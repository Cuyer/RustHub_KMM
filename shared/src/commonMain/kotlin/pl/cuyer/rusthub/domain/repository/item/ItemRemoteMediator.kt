package pl.cuyer.rusthub.domain.repository.item

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import database.ItemEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.ConnectivityException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.util.CrashReporter

@OptIn(ExperimentalPagingApi::class)
class ItemRemoteMediator(
    private val dataSource: ItemDataSource,
    private val api: ItemRepository,
    private val category: ItemCategory?,
    private val language: Language,
    private val searchQuery: String?
) : RemoteMediator<Int, ItemEntity>() {
    private var nextPage: Int = 0

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ItemEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                nextPage = 0
                0
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> nextPage
        }

        return try {
            when (val result = api.getItems(page, state.config.pageSize, category, language, searchQuery).first()) {
                is Result.Error -> {
                    return if (
                        result.exception is ConnectivityException ||
                        result.exception is ServiceUnavailableException
                    ) {
                        MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        MediatorResult.Error(result.exception)
                    }
                }
                is Result.Success -> {
                    dataSource.upsertItems(result.data.items)
                    val end = page >= result.data.totalPages - 1
                    nextPage = if (end) page else page + 1
                    MediatorResult.Success(endOfPaginationReached = end)
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
            return if (e is ConnectivityException || e is ServiceUnavailableException) {
                MediatorResult.Success(endOfPaginationReached = true)
            } else {
                MediatorResult.Error(e)
            }
        }
    }
}
