package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class PurchaseInfo(
    val productId: String,
    val purchaseToken: String
)
