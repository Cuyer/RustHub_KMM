package pl.cuyer.rusthub.android.util.composeUtil

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.LocaleListCompat
import pl.cuyer.rusthub.domain.model.Language
import java.util.Locale

@Composable
fun rememberCurrentLanguage(): State<Language> {
    val configuration = LocalConfiguration.current // recomposes on locale/config changes
    val language = remember(configuration) {
        val locales = AppCompatDelegate.getApplicationLocales()
        val tag = if (!locales.isEmpty) {
            locales[0]!!.language
        } else {
            Locale.getDefault().language
        }
        when (tag) {
            "pl" -> Language.POLISH
            "de" -> Language.GERMAN
            "fr" -> Language.FRENCH
            "ru" -> Language.RUSSIAN
            "pt" -> Language.PORTUGUESE
            "es" -> Language.SPANISH
            else -> Language.ENGLISH
        }
    }
    return rememberUpdatedState(language)
}