package pl.cuyer.rusthub.presentation.features.item

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.update
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.usecase.GetItemDetailsUseCase
import pl.cuyer.rusthub.util.getCurrentAppLanguage

class ItemDetailsViewModel(
    private val getItemDetailsUseCase: GetItemDetailsUseCase,
    private val id: Long?,
    private val name: String?,
) : BaseViewModel() {

    private val initialState = ItemDetailsState(id = id, name = name)
    private val _state = MutableStateFlow(initialState)
    private var detailsJob: Job? = null
    val state = _state
        .onStart { id?.let { observeItem(it) } }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value
        )

    private fun observeItem(id: Long) {
        detailsJob?.cancel()
        detailsJob = getItemDetailsUseCase(id, getCurrentAppLanguage())
            .onStart { updateLoading(true) }
            .catch { e ->
                if (e is CancellationException) throw e
                updateLoading(false)
            }
            .onEach { result ->
                when (result) {
                    is Result.Success -> _state.update {
                        it.copy(
                            item = result.data,
                            isLoading = false,
                            id = id,
                            name = result.data.name ?: it.name
                        )
                    }
                    is Result.Error -> updateLoading(false)
                }
            }
            .launchIn(coroutineScope)
    }

    fun refresh() {
        (_state.value.id ?: id)?.let { observeItem(it) }
    }

    private fun updateLoading(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }
}
