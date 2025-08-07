package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
enum class ItemLanguageDto {
    @SerialName("fr")
    FR,
    @SerialName("en")
    EN,
    @SerialName("de")
    DE,
    @SerialName("ru")
    RU,
    @SerialName("pt")
    @JsonNames("pt-BR")
    PT,
    @SerialName("es")
    @JsonNames("es-ES")
    ES,
    @SerialName("uk")
    @JsonNames("uk-UA")
    UK
}
