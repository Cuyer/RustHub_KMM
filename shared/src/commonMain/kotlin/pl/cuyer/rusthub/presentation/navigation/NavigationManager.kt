package pl.cuyer.rusthub.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NavigationManager {
    private var startDestination: NavKey = Onboarding
    private val _resetFlow = MutableSharedFlow<NavKey>(extraBufferCapacity = 1)
    val resetFlow = _resetFlow.asSharedFlow()

    fun setInitialDestination(destination: NavKey) {
        startDestination = destination
    }

    fun getStartDestination(): NavKey = startDestination

    fun resetRoot(destination: NavKey) {
        _resetFlow.tryEmit(destination)
    }
}
