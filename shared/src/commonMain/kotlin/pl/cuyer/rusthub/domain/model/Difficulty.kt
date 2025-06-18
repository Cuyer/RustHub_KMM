package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Difficulty {
    VANILLA, SOFTCORE, HARDCORE
}

val Difficulty.displayName: String
    get() = this.name.lowercase().replaceFirstChar { it.uppercase() }