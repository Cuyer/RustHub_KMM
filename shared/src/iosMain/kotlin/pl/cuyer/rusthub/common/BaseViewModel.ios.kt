package pl.cuyer.rusthub.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

actual abstract class BaseViewModel {
    actual val coroutineScope: CoroutineScope = MainScope()
}