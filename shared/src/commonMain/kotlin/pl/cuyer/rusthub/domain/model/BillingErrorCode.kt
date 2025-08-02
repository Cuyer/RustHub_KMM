package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

@Immutable
enum class BillingErrorCode {
    USER_CANCELED,
    SERVICE_UNAVAILABLE,
    BILLING_UNAVAILABLE,
    ITEM_UNAVAILABLE,
    DEVELOPER_ERROR,
    ERROR,
    ITEM_ALREADY_OWNED,
    ITEM_NOT_OWNED,
    FEATURE_NOT_SUPPORTED,
    NETWORK_ERROR,
    SERVICE_DISCONNECTED,
    UNKNOWN
}

fun BillingErrorCode.toMessage(stringProvider: StringProvider): String {
    return when (this) {
        BillingErrorCode.USER_CANCELED -> stringProvider.get(SharedRes.strings.purchase_canceled)
        BillingErrorCode.SERVICE_UNAVAILABLE, BillingErrorCode.SERVICE_DISCONNECTED -> stringProvider.get(SharedRes.strings.error_service_unavailable)
        BillingErrorCode.BILLING_UNAVAILABLE -> stringProvider.get(SharedRes.strings.error_billing_unavailable)
        BillingErrorCode.ITEM_UNAVAILABLE -> stringProvider.get(SharedRes.strings.error_item_unavailable)
        BillingErrorCode.DEVELOPER_ERROR -> stringProvider.get(SharedRes.strings.error_developer_error)
        BillingErrorCode.ERROR -> stringProvider.get(SharedRes.strings.error_billing_generic)
        BillingErrorCode.ITEM_ALREADY_OWNED -> stringProvider.get(SharedRes.strings.error_item_owned)
        BillingErrorCode.ITEM_NOT_OWNED -> stringProvider.get(SharedRes.strings.error_item_not_owned)
        BillingErrorCode.FEATURE_NOT_SUPPORTED -> stringProvider.get(SharedRes.strings.error_feature_not_supported)
        BillingErrorCode.NETWORK_ERROR -> stringProvider.get(SharedRes.strings.error_network)
        BillingErrorCode.UNKNOWN -> stringProvider.get(SharedRes.strings.error_unknown)
    }
}
