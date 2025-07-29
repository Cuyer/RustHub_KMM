package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan

import pl.cuyer.rusthub.domain.model.SubscriptionState


@Immutable
data class ActiveSubscription(
    val plan: SubscriptionPlan,
    val expiration: Instant?,
    val state: SubscriptionState
)
