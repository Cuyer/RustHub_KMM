package pl.cuyer.rusthub.domain.repository.subscription.network

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result

interface SubscriptionRepository {
    fun addSubscription(id: Long): Flow<Result<Unit>>
    fun removeSubscription(id: Long): Flow<Result<Unit>>
}
