package pl.cuyer.rusthub.presentation.features.startup

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import pl.cuyer.rusthub.presentation.navigation.ServerList

class StartupViewModel(
    private val getUserUseCase: GetUserUseCase,
) : BaseViewModel() {

    private val _state = MutableStateFlow(StartupState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = StartupState()
    )

    init {
        coroutineScope.launch {
            val user = getUserUseCase().firstOrNull()
            _state.value = StartupState(
                startDestination = if (user != null) ServerList else Onboarding,
                isLoading = false
            )
        }
    }
}
