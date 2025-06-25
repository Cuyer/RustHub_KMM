package pl.cuyer.rusthub.data.network.server.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.data.network.model.DifficultyDto
import pl.cuyer.rusthub.data.network.model.FlagDto
import pl.cuyer.rusthub.data.network.model.MapsDto
import pl.cuyer.rusthub.data.network.model.RegionDto
import pl.cuyer.rusthub.data.network.model.ServerStatusDto
import pl.cuyer.rusthub.data.network.model.WipeScheduleDto
import pl.cuyer.rusthub.data.network.model.WipeTypeDto

@Serializable
data class ServerInfoDto(
    val id: Long? = null,
    val name: String? = null,
    val wipe: Instant? = null,
    val ranking: Long? = null,
    val modded: Boolean? = null,
    @SerialName("player_count")
    val playerCount: Long? = null,
    @SerialName("server_capacity")
    val serverCapacity: Long? = null,
    @SerialName("map_name")
    val mapName: MapsDto? = null,
    val cycle: Double? = null,
    @SerialName("server_flag")
    val serverFlag: FlagDto? = null,
    val region: RegionDto? = null,
    @SerialName("max_group")
    val maxGroup: Long? = null,
    val difficulty: DifficultyDto? = null,
    @SerialName("wipe_schedule")
    val wipeSchedule: WipeScheduleDto? = null,
    val isOfficial: Boolean? = null,
    val serverIp: String? = null,
    @SerialName("map_image")
    val mapImage: String? = null,
    val description: String? = null,
    @SerialName("status")
    val serverStatus: ServerStatusDto? = null,
    @SerialName("wipe_type")
    val wipeType: WipeTypeDto? = null,
    val blueprints: Boolean? = null,
    val kits: Boolean? = null,
    val decay: Float? = null,
    val upkeep: Float? = null,
    val rates: Int? = null,
    val seed: Long? = null,
    @SerialName("map_size")
    val mapSize: Int? = null,
    val monuments: Int? = null,
    @SerialName("average_fps")
    val averageFps: Long? = null,
    val pve: Boolean? = null,
    val website: String? = null,
    @SerialName("is_premium")
    val isPremium: Boolean? = null,
    @SerialName("map_url")
    val mapUrl: String? = null,
    @SerialName("header_image")
    val headerImage: String? = null,
    @SerialName("is_favorite")
    val isFavorite: Boolean? = null,
    @SerialName("is_subscribed")
    val isSubscribed: Boolean? = null,
    @SerialName("next_wipe")
    val nextWipe: Instant? = null,
    @SerialName("next_map_wipe")
    val nextMapWipe: Instant? = null
)
