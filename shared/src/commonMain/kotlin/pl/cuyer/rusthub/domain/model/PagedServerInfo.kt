package pl.cuyer.rusthub.domain.model

data class PagedServerInfo(
    val servers: List<ServerInfo>,
    val size: Int,
    val totalPages: Int,
    val totalItems: Int,
)
