package pl.cuyer.rusthub.domain.repository.rustmap

import kotlinx.coroutines.flow.Flow
import pl.cuyer.domain.models.server.rustmap.MapResponse
import pl.cuyer.rusthub.common.Result

interface RustmapsClient {
    fun fetchMap(mapId: String): Flow<Result<MapResponse>>
}