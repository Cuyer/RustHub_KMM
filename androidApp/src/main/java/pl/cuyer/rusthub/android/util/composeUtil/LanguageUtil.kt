package pl.cuyer.rusthub.android.util.composeUtil

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalConfiguration
import pl.cuyer.rusthub.domain.model.Language
import java.util.Locale

@Composable
fun rememberCurrentLanguage(): State<Language> {
    val configuration = LocalConfiguration.current // recomposes on locale/config changes
    val language = remember(configuration) {
        val locale = runCatching {
            AppCompatDelegate.getApplicationLocales().takeIf { !it.isEmpty }?.get(0)
        }.getOrNull()
        val code = (locale ?: Locale.getDefault()).language
        when (code) {
            "pl" -> Language.POLISH
            "de" -> Language.GERMAN
            "fr" -> Language.FRENCH
            "ru" -> Language.RUSSIAN
            "pt" -> Language.PORTUGUESE
            "es" -> Language.SPANISH
            "uk" -> Language.UKRAINIAN
            else -> Language.ENGLISH
        }
    }
    return rememberUpdatedState(language)
}
