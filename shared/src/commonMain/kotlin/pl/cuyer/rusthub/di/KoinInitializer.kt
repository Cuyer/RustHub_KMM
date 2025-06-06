package pl.cuyer.rusthub.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.navigation.DefaultNavigator
import pl.cuyer.rusthub.navigation.Destination
import pl.cuyer.rusthub.navigation.Navigator

val appModule = module {
    single { RustHubDatabase(get()) }
    single<Navigator> {
        DefaultNavigator(
            startDestination = Destination.HomeGraph
        )
    }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    Napier.base(DebugAntilog())
    appDeclaration()
    modules(appModule, platformModule)
    printLogger()
}
