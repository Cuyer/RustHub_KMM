package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ItemLanguageDto {
    @SerialName("fr")
    FR,
    @SerialName("en")
    EN,
    @SerialName("de")
    DE,
    @SerialName("ru")
    RU
}
