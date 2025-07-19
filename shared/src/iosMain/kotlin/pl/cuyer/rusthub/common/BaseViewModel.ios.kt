package pl.cuyer.rusthub.common

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus

actual abstract class BaseViewModel {
    actual val exceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Napier.e("Unhandled coroutine exception", throwable)
        }

    actual val coroutineScope: CoroutineScope = MainScope() + exceptionHandler
}