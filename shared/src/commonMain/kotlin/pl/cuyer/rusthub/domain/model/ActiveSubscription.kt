package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan

@Immutable
data class ActiveSubscription(
    val plan: SubscriptionPlan,
    val expiration: Instant?
)
