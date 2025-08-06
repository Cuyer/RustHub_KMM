package pl.cuyer.rusthub.presentation.features.monument

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.MonumentType
import pl.cuyer.rusthub.domain.model.MonumentSyncState

@Immutable
data class MonumentState(
    val isRefreshing: Boolean = true,
    val searchText: String = "",
    val selectedType: MonumentType? = null,
    val syncState: MonumentSyncState = MonumentSyncState.DONE,
)
