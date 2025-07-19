package pl.cuyer.rusthub.common

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope

// commonMain
expect abstract class BaseViewModel() {
    val coroutineScope: CoroutineScope
    val exceptionHandler: CoroutineExceptionHandler
}
