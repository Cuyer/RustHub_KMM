package pl.cuyer.rusthub.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

// androidMain
actual abstract class BaseViewModel : ViewModel() {
    actual val coroutineScope: CoroutineScope
        get() = viewModelScope
}
