package pl.cuyer.rusthub.domain.model

enum class Language {
    ENGLISH,
    POLISH;

    companion object {
        fun fromDisplayName(name: String): Language? =
            entries.firstOrNull { it.displayName == name }
    }
}

val Language.displayName: String
    get() = when (this) {
        Language.ENGLISH -> "English"
        Language.POLISH -> "Polish"
    }
