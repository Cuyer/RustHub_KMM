package pl.cuyer.rusthub.presentation.features

import pl.cuyer.rusthub.presentation.model.FilterUi

data class ServerState(
    val isLoading: Boolean = false,
    val filters: FilterUi = FilterUi()
)