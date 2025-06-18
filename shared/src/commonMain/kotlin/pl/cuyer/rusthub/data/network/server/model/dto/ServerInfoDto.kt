package pl.cuyer.rusthub.data.network.server.model.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.data.model.DifficultyEntity
import pl.cuyer.rusthub.data.model.FlagEntity
import pl.cuyer.rusthub.data.model.MapsEntity
import pl.cuyer.rusthub.data.model.RegionEntity
import pl.cuyer.rusthub.data.model.WipeScheduleEntity

@Serializable
data class ServerInfoDto(
    val id: Long? = null,
    val name: String? = null,
    val wipe: Instant? = null,
    val ranking: Double? = null,
    val modded: Boolean? = null,
    @SerialName("player_count")
    val playerCount: Long? = null,
    @SerialName("server_capacity")
    val serverCapacity: Long? = null,
    @SerialName("map_name")
    val mapName: MapsEntity? = null,
    val cycle: Double? = null,
    @SerialName("server_flag")
    val serverFlag: FlagEntity? = null,
    val region: RegionEntity? = null,
    @SerialName("max_group")
    val maxGroup: Long? = null,
    val difficulty: DifficultyEntity? = null,
    @SerialName("wipe_schedule")
    val wipeSchedule: WipeScheduleEntity? = null,
    val isOfficial: Boolean? = null,
    val serverIp: String? = null,
    @SerialName("map_image")
    val mapImage: String? = null,
    val description: String? = null,
    val mapId: String? = null,
)
