package pl.cuyer.rusthub.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.DatabaseDriverFactory
import pl.cuyer.rusthub.data.network.HttpClientFactory
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.util.ClipboardHandler

actual val platformModule: Module = module {
    single<RustHubDatabase> { DatabaseDriverFactory().create() }
    single { HttpClientFactory(get()).create() }
    single { ClipboardHandler() }
}