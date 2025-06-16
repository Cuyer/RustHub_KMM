package pl.cuyer.rusthub.util
import pl.cuyer.rusthub.domain.model.Flag
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.localizedStringForCountryCode

actual fun getCountryDisplayName(countryCode: String): String {
    return NSLocale.currentLocale.localizedStringForCountryCode(countryCode) ?: countryCode
}

actual fun getCountryCode(displayName: String): String? {
    val locale = NSLocale.currentLocale
    val allCodes = Flag.entries.map { it.name }

    val target = displayName.trim().lowercase()
    return allCodes.firstOrNull { code ->
        locale.localizedStringForCountryCode(code)?.lowercase() == target
    }
}