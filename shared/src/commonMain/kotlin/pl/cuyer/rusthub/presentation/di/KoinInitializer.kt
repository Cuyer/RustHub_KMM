package pl.cuyer.rusthub.presentation.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.auth.AuthDataSourceImpl
import pl.cuyer.rusthub.data.local.filter.FiltersDataSourceImpl
import pl.cuyer.rusthub.data.local.filtersOptions.FiltersOptionsDataSourceImpl
import pl.cuyer.rusthub.data.local.remotekey.RemoteKeyDataSourceImpl
import pl.cuyer.rusthub.data.local.search.SearchQueryDataSourceImpl
import pl.cuyer.rusthub.data.local.server.ServerDataSourceImpl
import pl.cuyer.rusthub.data.network.auth.AuthRepositoryImpl
import pl.cuyer.rusthub.data.network.filtersOptions.FiltersOptionsClientImpl
import pl.cuyer.rusthub.data.network.server.ServerClientImpl
import pl.cuyer.rusthub.domain.repository.RemoteKeyDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsRepository
import pl.cuyer.rusthub.domain.repository.search.SearchQueryDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerRepository
import pl.cuyer.rusthub.domain.usecase.ClearFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersOptionsUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.GetSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetServerDetailsUseCase
import pl.cuyer.rusthub.domain.usecase.RegisterUserUseCase
import pl.cuyer.rusthub.domain.usecase.SaveFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.SaveSearchQueryUseCase
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController

val appModule = module {
    single<SnackbarController> { SnackbarController }
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            explicitNulls = false
        }
    }
    singleOf(::ServerClientImpl) bind ServerRepository::class
    singleOf(::ServerDataSourceImpl) bind ServerDataSource::class
    singleOf(::FiltersDataSourceImpl) bind FiltersDataSource::class
    singleOf(::SearchQueryDataSourceImpl) bind SearchQueryDataSource::class
    singleOf(::RemoteKeyDataSourceImpl) bind RemoteKeyDataSource::class
    single { GetPagedServersUseCase(get(), get(), get(), get()) }
    singleOf(::FiltersOptionsClientImpl) bind FiltersOptionsRepository::class
    singleOf(::FiltersOptionsDataSourceImpl) bind FiltersOptionsDataSource::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::AuthDataSourceImpl) bind AuthDataSource::class
    single { GetPagedServersUseCase(get(), get(), get(), get()) }
    single { GetFiltersUseCase(get()) }
    single { SaveFiltersUseCase(get()) }
    single { SaveSearchQueryUseCase(get()) }
    single { ClearFiltersUseCase(get()) }
    single { GetFiltersOptionsUseCase(get(), get()) }
    single { GetSearchQueriesUseCase(get()) }
    single { DeleteSearchQueriesUseCase(get()) }
    single { GetServerDetailsUseCase(get()) }
    single { RegisterUserUseCase(get(), get()) }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    Napier.base(DebugAntilog())
    appDeclaration()
    modules(appModule, platformModule)
    printLogger()
}
