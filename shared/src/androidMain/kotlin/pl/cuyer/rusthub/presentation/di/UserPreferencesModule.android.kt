package pl.cuyer.rusthub.presentation.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.cuyer.rusthub.UserPreferencesProto
import pl.cuyer.rusthub.data.local.user.RustHubPreferencesDataSource
import pl.cuyer.rusthub.data.local.user.UserPreferencesRepositoryImpl
import pl.cuyer.rusthub.presentation.di.UserPreferencesSerializer
import pl.cuyer.rusthub.domain.repository.user.UserPreferencesRepository

private fun createUserPreferencesDataStore(
    context: Context,
    userPreferencesSerializer: UserPreferencesSerializer,
): DataStore<UserPreferencesProto> =
    DataStoreFactory.create(
        serializer = userPreferencesSerializer,
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    ) {
        context.dataStoreFile("user_preferences.pb")
    }

actual val userPreferencesModule: Module = module {
    single { UserPreferencesSerializer() }
    single<DataStore<UserPreferencesProto>> { createUserPreferencesDataStore(androidContext(), get()) }
    single { RustHubPreferencesDataSource(get()) }
    single { UserPreferencesRepositoryImpl(get()) } bind UserPreferencesRepository::class
}
