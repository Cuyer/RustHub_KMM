package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.domain.models.server.rustmap.MapResponse
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.rustmap.RustmapsClient

class GetRustMapUseCase(
    private val repository: RustmapsClient
) {
    operator fun invoke(mapId: String): Flow<Result<MapResponse>> {
        return repository.fetchMap(mapId)
    }
}