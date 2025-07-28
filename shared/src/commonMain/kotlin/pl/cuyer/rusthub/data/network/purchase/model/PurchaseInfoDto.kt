package pl.cuyer.rusthub.data.network.purchase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
data class LineItemDto(
    val productId: String,
    val expiryTime: String? = null,
    val cancellationTime: String? = null,
    @SerialName("offerDetails")
    val offerDetails: OfferDetails? = null
)

@Serializable
data class OfferDetails(
    @SerialName("basePlanId")
    val basePlanId: String? = null,
    @SerialName("offerId")
    val offerId: String? = null
)

@Serializable
data class ExternalAccountIdentifiersDto(
    val obfuscatedAccountId: String? = null
)

@Serializable
data class SubscriptionInfoDto(
    val subscriptionState: SubscriptionStateDto? = null,
    val acknowledgementState: AcknowledgementStateDto? = null,
    val linkedPurchaseToken: String? = null,
    val lineItems: List<LineItemDto> = emptyList(),
    val externalAccountIdentifiers: ExternalAccountIdentifiersDto? = null
)

@Serializable
data class ProductInfoDto(
    val productId: String,
    val acknowledgementState: Int? = null,
    val purchaseState: Int? = null,
    val obfuscatedAccountId: String? = null
)

@Serializable
data class PurchaseInfoDto(
    val token: String,
    val username: String,
    val subscriptionState: SubscriptionStateDto? = null,
    val acknowledgementState: AcknowledgementStateDto? = null,
    val expiryTime: String? = null,
    val subscriptionInfo: SubscriptionInfoDto? = null,
    val productInfo: ProductInfoDto? = null,
    val createdAt: String? = null
)
