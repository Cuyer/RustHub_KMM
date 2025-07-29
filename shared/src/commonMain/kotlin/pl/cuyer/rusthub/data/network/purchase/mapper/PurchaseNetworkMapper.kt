package pl.cuyer.rusthub.data.network.purchase.mapper

import kotlinx.datetime.Instant
import pl.cuyer.rusthub.data.network.purchase.model.PurchaseInfoDto
import pl.cuyer.rusthub.data.network.purchase.model.SubscriptionStateDto
import pl.cuyer.rusthub.domain.model.ActiveSubscription
import pl.cuyer.rusthub.domain.model.SubscriptionState
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan

fun PurchaseInfoDto.toDomain(): ActiveSubscription? {
    val item = subscriptionInfo?.lineItems?.firstOrNull()
    val id = item?.offerDetails?.basePlanId ?: item?.productId ?: productInfo?.productId
    val plan = SubscriptionPlan.entries.firstOrNull { it.basePlanId == id || it.productId == id }
    val expiry = item?.expiryTime ?: expiryTime
    val expiration = expiry?.let { runCatching { Instant.parse(it) }.getOrNull() }
    val state = subscriptionInfo?.subscriptionState?.toDomain() ?: subscriptionState?.toDomain() ?: SubscriptionState.UNSPECIFIED
    return plan?.let { ActiveSubscription(it, expiration, state) }
}

fun SubscriptionStateDto.toDomain(): SubscriptionState = when (this) {
    SubscriptionStateDto.ACTIVE -> SubscriptionState.ACTIVE
    SubscriptionStateDto.PAUSED -> SubscriptionState.PAUSED
    SubscriptionStateDto.IN_GRACE_PERIOD -> SubscriptionState.IN_GRACE_PERIOD
    SubscriptionStateDto.ON_HOLD -> SubscriptionState.ON_HOLD
    SubscriptionStateDto.CANCELED -> SubscriptionState.CANCELED
    SubscriptionStateDto.EXPIRED -> SubscriptionState.EXPIRED
    SubscriptionStateDto.PENDING -> SubscriptionState.PENDING
    SubscriptionStateDto.UNSPECIFIED -> SubscriptionState.UNSPECIFIED
}
