package pl.cuyer.rusthub.common

import kotlinx.coroutines.CoroutineScope

// commonMain
expect abstract class BaseViewModel() {
    val coroutineScope: CoroutineScope
}
