package pl.cuyer.rusthub.presentation.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class FilterDropdownOption(
    val label: String,
    val options: List<String>,
    val selectedIndex: Int?
)

@Serializable
@Immutable
data class FilterCheckboxOption(
    val label: String,
    val isChecked: Boolean
)

@Serializable
@Immutable
data class FilterRangeOption(
    val label: String,
    val max: Int,
    val value: Int?
)
