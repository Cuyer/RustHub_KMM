package pl.cuyer.rusthub.presentation.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path
import okio.FileSystem
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.cuyer.rusthub.UserPreferencesProto
import pl.cuyer.rusthub.data.local.user.RustHubPreferencesDataSource
import pl.cuyer.rusthub.data.local.user.UserPreferencesRepositoryImpl
import pl.cuyer.rusthub.data.local.user.UserPreferencesSerializer
import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

private fun createUserPreferencesDataStore(
    path: Path,
    userPreferencesSerializer: UserPreferencesSerializer,
): DataStore<UserPreferencesProto> =
    DataStoreFactory.create(
        storage = userPreferencesSerializer,
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    )

actual val userPreferencesModule: Module = module {
    single { UserPreferencesSerializer() }
    single<DataStore<UserPreferencesProto>> {
        createUserPreferencesDataStore(
            FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "user_preferences.pb",
            get()
        )
    }
    single { RustHubPreferencesDataSource(get()) }
    single { UserPreferencesRepositoryImpl(get()) } bind UserPreferencesRepository::class
}
