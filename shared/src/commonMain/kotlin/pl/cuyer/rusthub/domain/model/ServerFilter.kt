package pl.cuyer.rusthub.domain.model

enum class ServerFilter {
    ALL,
    FAVOURITES,
    SUBSCRIBED;
}

val ServerFilter.displayName: String
    get() = when(this) {
        ServerFilter.ALL -> "All"
        ServerFilter.FAVOURITES -> "Favourites"
        ServerFilter.SUBSCRIBED -> "Subscribed"
    }
