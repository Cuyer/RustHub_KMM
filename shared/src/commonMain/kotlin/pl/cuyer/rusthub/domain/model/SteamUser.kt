package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SteamUser(
    val steamId: String,
    val avatar: String,
    val personaName: String,
    val personaState: Int,
    val lastLogoff: Long?,
    val gameId: String?
)

