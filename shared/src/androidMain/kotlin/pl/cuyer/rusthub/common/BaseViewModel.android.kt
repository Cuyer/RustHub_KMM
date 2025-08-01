package pl.cuyer.rusthub.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import pl.cuyer.rusthub.util.CrashReporter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus

// androidMain
actual abstract class BaseViewModel : ViewModel() {
    actual val exceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            CrashReporter.recordException(throwable)
            Napier.e("Unhandled coroutine exception", throwable)
        }

    actual val coroutineScope: CoroutineScope
        get() = viewModelScope + exceptionHandler
}
