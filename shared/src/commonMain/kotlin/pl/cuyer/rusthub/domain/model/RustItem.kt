package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable

// Nested item details
import pl.cuyer.rusthub.domain.model.Looting
import pl.cuyer.rusthub.domain.model.Crafting
import pl.cuyer.rusthub.domain.model.Recycling
import pl.cuyer.rusthub.domain.model.Raiding

@Serializable

data class RustItem(
    val slug: String? = null,
    val url: String? = null,
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val stackSize: Int? = null,
    val health: Int? = null,
    val categories: List<ItemCategory>? = null,
    val looting: List<Looting>? = null,
    val crafting: Crafting? = null,
    val recycling: Recycling? = null,
    val raiding: List<Raiding>? = null,
    val shortName: String? = null,
    val id: String? = null,
    val iconUrl: String? = null,
    val language: Language? = null,
)
