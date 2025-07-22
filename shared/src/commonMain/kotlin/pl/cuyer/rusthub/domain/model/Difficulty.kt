package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable
import androidx.compose.runtime.Immutable

@Serializable
@Immutable
enum class Difficulty {
    VANILLA, SOFTCORE, HARDCORE, PRIMITIVE;

    companion object {
        fun fromDisplayName(name: String): Difficulty? =
            Difficulty.entries.firstOrNull { it.displayName == name }
    }
}

val Difficulty.displayName: String
    get() = this.name.lowercase().replaceFirstChar { it.uppercase() }