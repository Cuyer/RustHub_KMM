package pl.cuyer.rusthub.di

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory

actual val platformModule: Module = module {
    single<SqlDriver> { DatabaseDriverFactory().create() }
}