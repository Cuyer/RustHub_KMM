package pl.cuyer.rusthub.presentation.features.monument

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.usecase.GetMonumentDetailsUseCase
import pl.cuyer.rusthub.util.getCurrentAppLanguage

class MonumentDetailsViewModel(
    private val getMonumentDetailsUseCase: GetMonumentDetailsUseCase,
    private val slug: String?,
) : BaseViewModel() {

    private val _state = MutableStateFlow(MonumentDetailsState())
    val state = _state
        .onStart { slug?.let { observeMonument(it) } }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value,
        )

    private var monumentJob: Job? = null

    private fun observeMonument(slug: String) {
        monumentJob?.cancel()

        monumentJob = getMonumentDetailsUseCase(slug, getCurrentAppLanguage())
            .onStart { updateLoading(true) }
            .catch { e ->
                if (e is CancellationException) throw e
                updateLoading(false)
            }
            .onEach { monument ->
                _state.update { it.copy(monument = monument, isLoading = false, slug = slug) }
            }
            .launchIn(coroutineScope)
    }

    private fun updateLoading(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }
}
