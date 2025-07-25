package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.model.Language

expect fun updateAppLanguage(language: Language)
expect fun getCurrentAppLanguage(): Language
