package pl.cuyer.rusthub.domain.model

data class RustItem(
    val slug: String? = null,
    val url: String? = null,
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val stackSize: Int? = null,
    val health: Int? = null,
    val categories: List<ItemCategory>? = null,
    val shortName: String? = null,
    val id: String? = null,
    val iconUrl: String? = null,
    val language: Language? = null,
)
