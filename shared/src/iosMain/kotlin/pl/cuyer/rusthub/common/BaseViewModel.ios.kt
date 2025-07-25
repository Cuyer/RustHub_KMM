package pl.cuyer.rusthub.common

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus
import pl.cuyer.rusthub.util.CrashReporter

actual abstract class BaseViewModel {
    actual val exceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            CrashReporter.recordException(throwable)
            Napier.e("Unhandled coroutine exception", throwable)
        }

    actual val coroutineScope: CoroutineScope = MainScope() + exceptionHandler
}