package pl.cuyer.rusthub.domain.usecase

import androidx.paging.Pager
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import database.Server
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.ServerDataSource

class GetPagedServersUseCase(
    private val dataSource: ServerDataSource
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(query: ServerQuery): Flow<PagingData<Server>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dataSource.getServersPagingSource(query) }
        ).flow
    }
}