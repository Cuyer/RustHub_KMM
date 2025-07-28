package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

@Serializable
@Immutable
enum class SubscriptionState {
    UNSPECIFIED,
    PENDING,
    ACTIVE,
    PAUSED,
    IN_GRACE_PERIOD,
    ON_HOLD,
    CANCELED,
    EXPIRED
}

fun SubscriptionState.displayName(stringProvider: StringProvider): String = when (this) {
    SubscriptionState.ACTIVE -> stringProvider.get(SharedRes.strings.status_active)
    SubscriptionState.PAUSED, SubscriptionState.ON_HOLD -> stringProvider.get(SharedRes.strings.status_paused)
    SubscriptionState.IN_GRACE_PERIOD -> stringProvider.get(SharedRes.strings.status_in_grace)
    SubscriptionState.CANCELED -> stringProvider.get(SharedRes.strings.status_canceled)
    SubscriptionState.EXPIRED -> stringProvider.get(SharedRes.strings.status_expired)
    SubscriptionState.PENDING -> stringProvider.get(SharedRes.strings.status_pending)
    SubscriptionState.UNSPECIFIED -> stringProvider.get(SharedRes.strings.status_unknown)
}
