package pl.cuyer.rusthub.data.local.user

import androidx.datastore.core.DataMigration

object IntToStringIdsMigration : DataMigration<UserPreferencesProto> {
    override suspend fun shouldMigrate(currentData: UserPreferencesProto) = false
    override suspend fun migrate(currentData: UserPreferencesProto) = currentData
    override suspend fun cleanUp() = Unit
}
