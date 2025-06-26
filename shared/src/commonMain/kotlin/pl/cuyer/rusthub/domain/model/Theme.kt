package pl.cuyer.rusthub.domain.model

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM;

    companion object {
        fun fromDisplayName(name: String): Theme? =
            entries.firstOrNull { it.displayName == name }
    }
}

val Theme.displayName: String
    get() = when (this) {
        Theme.LIGHT -> "Light"
        Theme.DARK -> "Dark"
        Theme.SYSTEM -> "System"
    }
