package pl.cuyer.rusthub.domain.model

data class Settings(
    val theme: Theme,
    val language: Language,
    val biometricsEnabled: Boolean
)
