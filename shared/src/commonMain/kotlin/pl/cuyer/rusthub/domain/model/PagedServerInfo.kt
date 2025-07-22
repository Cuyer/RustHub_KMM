package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class PagedServerInfo(
    val servers: List<ServerInfo>,
    val size: Int,
    val totalPages: Int,
    val totalItems: Int,
)
