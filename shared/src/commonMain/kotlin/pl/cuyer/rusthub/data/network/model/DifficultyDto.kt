package pl.cuyer.rusthub.data.network.model

import kotlinx.serialization.Serializable

@Serializable
enum class DifficultyDto {
    VANILLA, SOFTCORE, HARDCORE, PRIMITIVE
}