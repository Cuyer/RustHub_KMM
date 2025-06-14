package domain.models.server.battlemetrics


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attributes(
    @SerialName("country")
    val country: String?,
    @SerialName("createdAt")
    val createdAt: String?,
    @SerialName("details")
    val details: Details?,
    @SerialName("id")
    val id: String,
    @SerialName("ip")
    val ip: String?,
    @SerialName("maxPlayers")
    val maxPlayers: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("players")
    val players: Int?,
    @SerialName("port")
    val port: Int?,
    @SerialName("rank")
    val rank: Int?,
    @SerialName("status")
    val status: String?,
    @SerialName("updatedAt")
    val updatedAt: String?
)