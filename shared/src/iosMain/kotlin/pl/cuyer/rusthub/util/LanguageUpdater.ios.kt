package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.model.Language

actual fun updateAppLanguage(language: Language) { /* No-op on iOS */ }
actual fun getCurrentAppLanguage(): Language = Language.ENGLISH
