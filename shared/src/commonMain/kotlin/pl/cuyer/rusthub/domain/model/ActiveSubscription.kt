package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlin.time.Instant
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan

import pl.cuyer.rusthub.domain.model.SubscriptionState
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@Immutable
data class ActiveSubscription(
    val plan: SubscriptionPlan,
    val expiration: Instant?,
    val state: SubscriptionState
)
