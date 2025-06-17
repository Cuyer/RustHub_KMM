package pl.cuyer.rusthub.domain.repository.server

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import database.ServerEntity
import pl.cuyer.rusthub.domain.repository.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.ServerDataSource

@OptIn(ExperimentalPagingApi::class)
class ServerRemoteMediator(
    private val dataSource: ServerDataSource,
    private val api: ServerRepository,
    private val filters: FiltersDataSource
) : RemoteMediator<Int, ServerEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ServerEntity>
    ): MediatorResult {

    }
}