package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Difficulty {
    VANILLA, SOFTCORE, HARDCORE
}