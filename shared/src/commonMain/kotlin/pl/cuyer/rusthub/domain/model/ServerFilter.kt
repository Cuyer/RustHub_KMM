package pl.cuyer.rusthub.domain.model

enum class ServerFilter {
    ALL,
    FAVORITES,
    SUBSCRIBED;
}

val ServerFilter.displayName: String
    get() = when(this) {
        ServerFilter.ALL -> "All"
        ServerFilter.FAVORITES -> "Favorites"
        ServerFilter.SUBSCRIBED -> "Subscribed"
    }
