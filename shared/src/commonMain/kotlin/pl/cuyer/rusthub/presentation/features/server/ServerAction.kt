package pl.cuyer.rusthub.presentation.features.server

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.ServerFilter

@Immutable
sealed interface ServerAction {
    @Immutable
    data class OnServerClick(val id: Long, val name: String) : ServerAction
    @Immutable
    data class OnLongServerClick(val ipAddress: String?) : ServerAction
    @Immutable
    data class OnSearch(val query: String) : ServerAction
    @Immutable
    data object OnClearFilters: ServerAction
    @Immutable
    data object DeleteSearchQueries : ServerAction
    @Immutable
    data class DeleteSearchQueryByQuery(val query: String) : ServerAction
    @Immutable
    data object OnClearSearchQuery : ServerAction
    @Immutable
    data class OnError(val exception: Throwable): ServerAction
    @Immutable
    data class OnFilterChange(val filter: ServerFilter): ServerAction
    @Immutable
    data class GatherConsent(val activity: Any, val onAdAvailable: () -> Unit) : ServerAction
    @Immutable
    data class OnDropdownChange(val index: Int, val selectedIndex: Int?) : ServerAction
    @Immutable
    data class OnCheckboxChange(val index: Int, val isChecked: Boolean) : ServerAction
    @Immutable
    data class OnRangeChange(val index: Int, val value: Int?) : ServerAction
    @Immutable
    data object RefreshOptions : ServerAction
}
