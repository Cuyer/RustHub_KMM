package pl.cuyer.rusthub.di

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.dsl.bind
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory
import pl.cuyer.rusthub.data.network.HttpClientFactory

actual val platformModule: Module = module {
    single<SqlDriver> { DatabaseDriverFactory().create() }
    single { HttpClientFactory(get()).create() }
}