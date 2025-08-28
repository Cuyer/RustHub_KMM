package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
enum class Maps {
    CUSTOM,
    PROCEDURAL,
    BARREN,
    CRAGGY_ISLAND,
    HAPPIS_ISLAND,
    SAVAS_ISLAND_KOTH,
    SAVAS_ISLAND;

    companion object {
        fun fromDisplayName(name: String): Maps? =
            entries.firstOrNull { it.displayName == name }
    }
}

val Maps.displayName: String
    get() = this.name.lowercase().replaceFirstChar { it.uppercase() }