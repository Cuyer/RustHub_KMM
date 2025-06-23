package pl.cuyer.rusthub.domain.repository.favourite.network

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result

interface FavouriteRepository {
    fun addFavourite(id: Long): Flow<Result<Unit>>
    fun removeFavourite(id: Long): Flow<Result<Unit>>
}
