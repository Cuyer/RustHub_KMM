package pl.cuyer.rusthub.presentation.model

import dev.icerock.moko.resources.StringResource
import pl.cuyer.rusthub.SharedRes
import kotlinx.serialization.Serializable
import androidx.compose.runtime.Immutable

@Serializable
@Immutable
enum class SubscriptionPlan(
    val productId: String,
    val basePlanId: String?,
    val label: StringResource,
    val billed: StringResource
) {
    MONTHLY("rusthub_pro", "pro-monthly", SharedRes.strings.monthly, SharedRes.strings.billed_monthly),
    YEARLY("rusthub_pro", "pro-yearly", SharedRes.strings.yearly, SharedRes.strings.billed_yearly),
    LIFETIME("pro_lifetime", null, SharedRes.strings.lifetime, SharedRes.strings.pay_once); // No basePlanId for INAPP

    companion object {
        const val SUBSCRIPTION_ID = "rusthub_pro"
    }
}
