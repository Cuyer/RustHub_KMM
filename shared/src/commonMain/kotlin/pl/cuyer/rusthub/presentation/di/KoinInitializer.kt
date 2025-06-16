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
import pl.cuyer.rusthub.data.local.remoteKey.RemoteKeyDataSourceImpl
import pl.cuyer.rusthub.data.local.server.ServerDataSourceImpl
import pl.cuyer.rusthub.data.local.timestamp.TimestampDataSourceImpl
import pl.cuyer.rusthub.data.network.battlemetrics.BattlemetricsClientImpl
import pl.cuyer.rusthub.data.network.rustmap.RustmapsClientImpl
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsClient
import pl.cuyer.rusthub.domain.repository.remoteKey.RemoteKeyDataSource
import pl.cuyer.rusthub.domain.repository.rustmap.RustmapsClient
import pl.cuyer.rusthub.domain.repository.timestamp.TimestampDataSource
import pl.cuyer.rusthub.domain.usecase.FetchAllServersUseCase
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.PrepareRustMapUseCase
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
    singleOf(::BattlemetricsClientImpl) bind BattlemetricsClient::class
    singleOf(::RustmapsClientImpl) bind RustmapsClient::class
    singleOf(::ServerDataSourceImpl) bind ServerDataSource::class
    singleOf(::RemoteKeyDataSourceImpl) bind RemoteKeyDataSource::class
    singleOf(::TimestampDataSourceImpl) bind TimestampDataSource::class
    single { GetPagedServersUseCase (get()) }
    single { FetchAllServersUseCase (get(), get(), get(), get(), get()) }
    single { PrepareRustMapUseCase (get(), get()) }
    factoryOf(::ServerViewModel)
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    Napier.base(DebugAntilog())
    appDeclaration()
    modules(appModule, platformModule)
    printLogger()
}
