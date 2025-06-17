package pl.cuyer.rusthub.presentation.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.server.ServerDataSourceImpl
import pl.cuyer.rusthub.data.network.server.ServerClientImpl
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerRepository
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.presentation.features.ServerViewModel
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
    single { GetPagedServersUseCase (get()) }
    factoryOf(::ServerViewModel)
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    Napier.base(DebugAntilog())
    appDeclaration()
    modules(appModule, platformModule)
    printLogger()
}
