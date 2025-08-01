package pl.cuyer.rusthub.data.local.model

import kotlinx.serialization.Serializable

@Serializable
enum class OrderEntity { WIPE, RANK, NEXT_WIPE, PLAYER_COUNT }

