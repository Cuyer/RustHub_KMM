package pl.cuyer.rusthub.presentation.features.item

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.usecase.GetItemDetailsUseCase

class ItemDetailsViewModel(
    private val getItemDetailsUseCase: GetItemDetailsUseCase,
    private val itemId: Long?,
) : BaseViewModel() {

    private val _state = MutableStateFlow(ItemDetailsState())
    val state = _state
        .onStart { itemId?.let { observeItem(it) } }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ItemDetailsState()
        )

    private fun observeItem(id: Long) {
        getItemDetailsUseCase(id)
            .onStart { updateLoading(true) }
            .catch { updateLoading(false) }
            .onEach { item ->
                _state.update { it.copy(item = item, isLoading = false, itemId = id) }
            }
            .launchIn(coroutineScope)
    }

    private fun updateLoading(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }
}
