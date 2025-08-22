package pl.cuyer.rusthub.presentation.features.raid

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.SteamUser

@Immutable
data class RaidFormState(
    val id: String? = null,
    val name: String = "",
    val dateTime: String = "",
    val steamIds: List<String> = emptyList(),
    val description: String = "",
    val nameError: Boolean = false,
    val searchDialogVisible: Boolean = false,
    val searchQuery: String = "",
    val searchLoading: Boolean = false,
    val foundUsers: List<SteamUser> = emptyList(),
    val selectedFoundIds: Set<String> = emptySet(),
    val searchNotFound: Boolean = false,
    val isSaving: Boolean = false
)
