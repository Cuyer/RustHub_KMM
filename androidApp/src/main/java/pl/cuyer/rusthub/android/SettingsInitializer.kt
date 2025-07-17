package pl.cuyer.rusthub.android

import android.content.Context
import androidx.startup.Initializer
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.util.updateAppLanguage
import pl.cuyer.rusthub.util.updateAppTheme

class SettingsInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val dataStore = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
        val themeKey = stringPreferencesKey("theme")
        val languageKey = stringPreferencesKey("language")
        runBlocking {
            val prefs = dataStore.data.first()
            prefs[themeKey]?.let { updateAppTheme(Theme.valueOf(it)) }
            prefs[languageKey]?.let { updateAppLanguage(Language.valueOf(it)) }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
