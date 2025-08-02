package pl.cuyer.rusthub.data.network.purchase.mapper

import kotlinx.datetime.Instant
import pl.cuyer.rusthub.data.network.purchase.model.PurchaseInfoDto
import pl.cuyer.rusthub.data.network.purchase.model.PurchaseStateDto
import pl.cuyer.rusthub.data.network.purchase.model.SubscriptionStateDto
import pl.cuyer.rusthub.domain.model.ActiveSubscription
import pl.cuyer.rusthub.domain.model.SubscriptionState
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan

fun PurchaseInfoDto.toDomain(): ActiveSubscription? {
    val subItem = subscriptionInfo?.lineItems?.firstOrNull()
    val prodItem = productInfo?.productLineItems?.firstOrNull()
    val id =
        subItem?.offerDetails?.basePlanId ?: subItem?.productId ?: prodItem?.productId
            ?: productInfo?.productId
    val plan =
        SubscriptionPlan.entries.firstOrNull { it.basePlanId == id || it.productId == id }
    val expiry = subItem?.expiryTime ?: expiryTime
    val expiration = expiry?.let { runCatching { Instant.parse(it) }.getOrNull() }
    val state =
        subscriptionInfo?.subscriptionState?.toDomain() ?: subscriptionState?.toDomain()
            ?: productInfo?.purchaseStateContext?.purchaseState?.toDomain()
            ?: SubscriptionState.UNSPECIFIED
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

fun PurchaseStateDto.toDomain(): SubscriptionState = when (this) {
    PurchaseStateDto.PURCHASED -> SubscriptionState.ACTIVE
    PurchaseStateDto.PENDING -> SubscriptionState.PENDING
    PurchaseStateDto.CANCELED -> SubscriptionState.CANCELED
    PurchaseStateDto.UNSPECIFIED -> SubscriptionState.UNSPECIFIED
}
