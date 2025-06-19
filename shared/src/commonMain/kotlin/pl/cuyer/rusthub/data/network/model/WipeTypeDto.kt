package pl.cuyer.rusthub.data.network.model

import kotlinx.serialization.Serializable

@Serializable
enum class WipeTypeDto {
    MAP, FULL, BP, UNKNOWN
}