package pl.cuyer.rusthub.data.network.purchase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames

@Serializable
enum class SubscriptionStateDto {
    @SerialName("SUBSCRIPTION_STATE_UNSPECIFIED")
    @JsonNames("UNSPECIFIED")
    UNSPECIFIED,
    @SerialName("SUBSCRIPTION_STATE_PENDING")
    @JsonNames("PENDING")
    PENDING,
    @SerialName("SUBSCRIPTION_STATE_ACTIVE")
    @JsonNames("ACTIVE")
    ACTIVE,
    @SerialName("SUBSCRIPTION_STATE_PAUSED")
    @JsonNames("PAUSED")
    PAUSED,
    @SerialName("SUBSCRIPTION_STATE_IN_GRACE_PERIOD")
    @JsonNames("IN_GRACE_PERIOD")
    IN_GRACE_PERIOD,
    @SerialName("SUBSCRIPTION_STATE_ON_HOLD")
    @JsonNames("ON_HOLD")
    ON_HOLD,
    @SerialName("SUBSCRIPTION_STATE_CANCELED")
    @JsonNames("CANCELED")
    CANCELED,
    @SerialName("SUBSCRIPTION_STATE_EXPIRED")
    @JsonNames("EXPIRED")
    EXPIRED
}

@Serializable
enum class AcknowledgementStateDto {
    @SerialName("ACKNOWLEDGEMENT_STATE_UNSPECIFIED")
    @JsonNames("UNSPECIFIED")
    UNSPECIFIED,
    @SerialName("ACKNOWLEDGEMENT_STATE_PENDING")
    @JsonNames("PENDING")
    PENDING,
    @SerialName("ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED")
    @JsonNames("ACKNOWLEDGED")
    ACKNOWLEDGED
}

@Serializable
enum class PurchaseStateDto {
    @SerialName("PURCHASE_STATE_UNSPECIFIED")
    @JsonNames("UNSPECIFIED")
    UNSPECIFIED,
    @SerialName("PURCHASED")
    PURCHASED,
    @SerialName("PENDING")
    PENDING,
    @SerialName("CANCELED")
    CANCELED
}

@Serializable
data class PurchaseStateContextDto(
    val purchaseState: PurchaseStateDto? = null
)

@Serializable
data class SubscriptionLineItemDto(
    @SerialName("productId") val productId: String,
    @SerialName("expiryTime") val expiryTime: String? = null,
    @SerialName("cancellationTime") val cancellationTime: String? = null,
    @SerialName("offerDetails") val offerDetails: OfferDetailsDto? = null
)

@Serializable
data class ProductLineItemDto(
    @SerialName("productId") val productId: String? = null
)

@Serializable
data class OfferDetailsDto(
    @SerialName("basePlanId") val basePlanId: String? = null,
    @SerialName("offerId") val offerId: String? = null
)

@Serializable
data class TestPurchaseContextDto(
    @SerialName("fopType") val fopType: String? = null
)

@Serializable
data class ExternalAccountIdentifiersDto(
    @SerialName("obfuscatedExternalAccountId") val obfuscatedAccountId: String? = null
)

@Serializable
data class SubscriptionPurchaseInfoDto(
    val subscriptionState: SubscriptionStateDto? = null,
    val acknowledgementState: AcknowledgementStateDto? = null,
    val linkedPurchaseToken: String? = null,
    val lineItems: List<SubscriptionLineItemDto> = emptyList(),
    val externalAccountIdentifiers: ExternalAccountIdentifiersDto? = null
)

@Serializable
data class ProductPurchaseInfoDto(
    val acknowledgementState: AcknowledgementStateDto? = null,
    @SerialName("obfuscatedExternalAccountId") val obfuscatedAccountId: String? = null,
    @SerialName("productLineItem") val productLineItems: List<ProductLineItemDto> = emptyList(),
    val purchaseStateContext: PurchaseStateContextDto? = null,
    val testPurchaseContext: TestPurchaseContextDto? = null
) {
    @Transient
    val productId: String? = productLineItems.firstOrNull()?.productId

    @Transient
    val purchaseState: PurchaseStateDto? = purchaseStateContext?.purchaseState
}

@Serializable
data class PurchaseInfoDto(
    val token: String,
    val username: String,
    val subscriptionState: SubscriptionStateDto? = null,
    val acknowledgementState: AcknowledgementStateDto? = null,
    val expiryTime: String? = null,
    val subscriptionInfo: SubscriptionPurchaseInfoDto? = null,
    val productInfo: ProductPurchaseInfoDto? = null,
    val createdAt: String? = null
)
