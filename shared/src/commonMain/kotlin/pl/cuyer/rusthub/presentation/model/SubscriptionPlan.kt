package pl.cuyer.rusthub.presentation.model

import dev.icerock.moko.resources.StringResource
import pl.cuyer.rusthub.SharedRes

enum class SubscriptionPlan(val productId: String, val label: StringResource, val billed: StringResource) {
    MONTHLY("pro_monthly", SharedRes.strings.monthly, SharedRes.strings.billed_monthly),
    YEARLY("pro_yearly", SharedRes.strings.yearly, SharedRes.strings.billed_yearly),
    LIFETIME("pro_lifetime", SharedRes.strings.lifetime, SharedRes.strings.pay_once)
}
