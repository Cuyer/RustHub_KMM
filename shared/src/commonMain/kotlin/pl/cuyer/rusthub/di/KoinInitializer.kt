package pl.cuyer.rusthub.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pl.cuyer.rusthub.data.network.battlemetrics.BattlemetricsClientImpl
import pl.cuyer.rusthub.data.network.rustmap.RustmapsClientImpl
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsClient
import pl.cuyer.rusthub.domain.repository.rustmap.RustmapsClient
import pl.cuyer.rusthub.navigation.DefaultNavigator
import pl.cuyer.rusthub.navigation.Destination
import pl.cuyer.rusthub.navigation.Navigator

val appModule = module {
    single { RustHubDatabase(get()) }
    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
    single<Navigator> {
        DefaultNavigator(
            startDestination = Destination.HomeGraph
        )
    }
    singleOf(::BattlemetricsClientImpl) bind BattlemetricsClient::class
    singleOf(::RustmapsClientImpl) bind RustmapsClient::class
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    Napier.base(DebugAntilog())
    appDeclaration()
    modules(appModule, platformModule)
    printLogger()
}
