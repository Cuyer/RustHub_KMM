package pl.cuyer.rusthub.util

import java.util.Locale

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
    val locales = Locale.getISOCountries()
    val target = displayName.trim().lowercase(Locale.getDefault())

    return locales.firstOrNull { code ->
        val name = Locale.Builder().setRegion(code).build()
            .getDisplayCountry(Locale.getDefault())
            .lowercase(Locale.getDefault())

        name == target
    }
}