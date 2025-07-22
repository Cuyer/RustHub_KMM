package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Settings(
    val theme: Theme,
    val language: Language
)
