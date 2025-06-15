package pl.cuyer.rusthub.presentation.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.server.ServerDataSourceImpl
import pl.cuyer.rusthub.data.network.battlemetrics.BattlemetricsClientImpl
import pl.cuyer.rusthub.data.network.rustmap.RustmapsClientImpl
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsClient
import pl.cuyer.rusthub.domain.repository.rustmap.RustmapsClient
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.PrepareRustMapUseCase
import pl.cuyer.rusthub.presentation.features.ServerViewModel
import pl.cuyer.rusthub.presentation.navigation.DefaultNavigator
import pl.cuyer.rusthub.presentation.navigation.Destination
import pl.cuyer.rusthub.presentation.navigation.Navigator

val appModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            explicitNulls = false
        }
    }
    single<Navigator> {
        DefaultNavigator(
            startDestination = Destination.HomeGraph
        )
    }
    singleOf(::BattlemetricsClientImpl) bind BattlemetricsClient::class
    singleOf(::RustmapsClientImpl) bind RustmapsClient::class
    singleOf(::ServerDataSourceImpl) bind ServerDataSource::class
    single { GetPagedServersUseCase (get(), get()) }
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
