package pl.cuyer.rusthub.domain.model

enum class Order {
    WIPE,
    RANK,
    PLAYER_COUNT
}

val Order.displayName: String
    get() = when (this) {
        Order.WIPE -> "Last wiped"
        Order.RANK -> "Ranking"
        Order.PLAYER_COUNT -> "Player count"
    }