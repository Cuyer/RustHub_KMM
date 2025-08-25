package pl.cuyer.rusthub.presentation.features.item

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.usecase.GetItemDetailsUseCase
import pl.cuyer.rusthub.util.getCurrentAppLanguage

class ItemDetailsViewModel(
    private val getItemDetailsUseCase: GetItemDetailsUseCase,
    private val slug: String?,
    private val name: String?,
) : BaseViewModel() {

    private val initialState = ItemDetailsState(slug = slug, name = name)
    private val _state = MutableStateFlow(initialState)
    val state = _state
        .onStart { slug?.let { observeItem(it) } }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = initialState
        )

    private fun observeItem(slug: String) {
        getItemDetailsUseCase(slug, getCurrentAppLanguage())
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
                            slug = slug,
                            name = result.data.name ?: it.name
                        )
                    }
                    is Result.Error -> updateLoading(false)
                }
            }
            .launchIn(coroutineScope)
    }

    private fun updateLoading(loading: Boolean) {
        _state.update { it.copy(isLoading = loading) }
    }
}
