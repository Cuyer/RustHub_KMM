package domain.models.server.battlemetrics


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Details(
    @SerialName("map")
    val map: String?,
    @SerialName("official")
    val official: Boolean?,
    @SerialName("pve")
    val pve: Boolean?,
    @SerialName("rust_description")
    val rustDescription: String?,
    @SerialName("rust_fps_avg")
    val rustFpsAvg: Double?,
    @SerialName("rust_gamemode")
    val rustGamemode: String?,
    @SerialName("rust_headerimage")
    val rustHeaderimage: String?,
    @SerialName("rust_last_wipe")
    val rustLastWipe: String?,
    @SerialName("rust_maps")
    val rustMaps: RustMaps?,
    @SerialName("rust_type")
    val rustType: String?,
    @SerialName("rust_next_wipe")
    val rustNextWipe: String?,
    @SerialName("rust_next_wipe_map")
    val rustNextWipeMap: String?,
    @SerialName("rust_premium")
    val rustPremium: Boolean?,
    @SerialName("rust_queued_players")
    val rustQueuedPlayers: Int?,
    @SerialName("rust_settings")
    val rustSettings: RustSettings?,
    @SerialName("rust_url")
    val rustUrl: String?,
    @SerialName("rust_wipes")
    val rustWipes: List<RustWipe> = emptyList(),
    @SerialName("rust_world_seed")
    val rustWorldSeed: Int?,
    @SerialName("rust_world_size")
    val rustWorldSize: Int?
)