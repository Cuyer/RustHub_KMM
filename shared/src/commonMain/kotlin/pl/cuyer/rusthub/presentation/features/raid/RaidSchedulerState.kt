package pl.cuyer.rusthub.presentation.features.raid

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.Raid

@Immutable
data class RaidSchedulerState(
    val raids: List<Raid> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val showForm: Boolean = false,
    val editingRaid: Raid? = null
)
