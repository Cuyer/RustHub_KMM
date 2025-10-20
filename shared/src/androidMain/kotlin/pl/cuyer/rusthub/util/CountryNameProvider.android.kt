package pl.cuyer.rusthub.util

import java.util.Locale

private val countryDisplayNameToCode: Map<String, String> by lazy {
    val locale = Locale.getDefault()
    Locale.getISOCountries().associateBy { code ->
        val displayName = Locale.Builder()
            .setRegion(code)
            .build()
            .getDisplayCountry(locale)
            .lowercase(locale)
        displayName
    }
}

actual fun getCountryDisplayName(countryCode: String): String {
    return try {
        Locale.Builder()
            .setRegion(countryCode)
            .build()
            .getDisplayCountry(Locale.getDefault())
    } catch (e: Exception) {
        countryCode
    }
}

actual fun getCountryCode(displayName: String): String? {
    val locale = Locale.getDefault()
    val target = displayName.trim().lowercase(locale)

    return countryDisplayNameToCode[target]
}
