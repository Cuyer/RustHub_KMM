package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.rustmap.RustmapsClient

class PrepareRustMapUseCase(
    private val repository: RustmapsClient,
    private val serverDataSource: ServerDataSource
) {
    operator fun invoke(mapId: String, serverId: Long): Flow<Result<Unit>> = channelFlow {
        repository.fetchMap(mapId).collectLatest { result ->
            when(result) {
                is Result.Success -> {
                    try {
                        serverDataSource.updateMap(id = serverId, mapImage = result.data.data.url)
                        send(Result.Success(Unit))
                    } catch (e: Exception) {
                        send(Result.Error(e))
                    }
                }
                is Result.Loading -> send(Result.Loading)
                is Result.Error -> send(Result.Error(result.exception))
            }
        }
    }
}