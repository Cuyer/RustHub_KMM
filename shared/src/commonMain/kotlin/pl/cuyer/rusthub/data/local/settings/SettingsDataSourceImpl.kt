package pl.cuyer.rusthub.data.local.settings

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.common.Constants.DEFAULT_KEY
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toSettings
import pl.cuyer.rusthub.data.local.mapper.toEntity
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.repository.settings.SettingsDataSource

class SettingsDataSourceImpl(
    db: RustHubDatabase
) : SettingsDataSource, Queries(db) {

    override fun getSettings(): Flow<Settings?> {
        return queries.getSettings(DEFAULT_KEY)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toSettings() }
    }

    override suspend fun upsertSettings(settings: Settings) {
        withContext(Dispatchers.IO) {
            queries.upsertSettings(
                id = DEFAULT_KEY,
                theme = settings.theme.toEntity(),
                language = settings.language.toEntity()
            )
        }
    }
}
