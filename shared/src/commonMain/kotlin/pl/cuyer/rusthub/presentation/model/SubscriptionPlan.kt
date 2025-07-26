package pl.cuyer.rusthub.presentation.model

import dev.icerock.moko.resources.StringResource
import pl.cuyer.rusthub.SharedRes

enum class SubscriptionPlan(val basePlanId: String, val label: StringResource, val billed: StringResource) {
    MONTHLY("pro-monthly", SharedRes.strings.monthly, SharedRes.strings.billed_monthly),
    YEARLY("pro-yearly", SharedRes.strings.yearly, SharedRes.strings.billed_yearly),
    LIFETIME("pro-lifetime", SharedRes.strings.lifetime, SharedRes.strings.pay_once);

    companion object {
        const val SUBSCRIPTION_ID = "rusthub_pro"
    }
}
