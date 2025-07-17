package pl.cuyer.rusthub.data.local.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual fun createSettingsDataStore(context: Any?): DataStore<Preferences> {
    require(context is Context)
    return createDataStore {
        context.filesDir.resolve(SETTINGS_DATASTORE_FILE).absolutePath
    }
}
