package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable
import androidx.compose.runtime.Immutable

// Nested item details
import pl.cuyer.rusthub.domain.model.Looting
import pl.cuyer.rusthub.domain.model.Crafting
import pl.cuyer.rusthub.domain.model.Recycling
import pl.cuyer.rusthub.domain.model.Raiding
import pl.cuyer.rusthub.domain.model.LootContent
import pl.cuyer.rusthub.domain.model.WhereToFind

@Serializable

@Immutable
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
    val lootContents: List<LootContent>? = null,
    val whereToFind: List<WhereToFind>? = null,
    val crafting: Crafting? = null,
    val recycling: Recycling? = null,
    val raiding: List<Raiding>? = null,
    val shortName: String? = null,
    val id: Long? = null,
    val iconUrl: String? = null,
    val language: Language? = null,
)
