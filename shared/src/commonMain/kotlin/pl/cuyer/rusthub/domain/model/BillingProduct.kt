package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class BillingProduct(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val hasFreeTrial: Boolean = false
)
