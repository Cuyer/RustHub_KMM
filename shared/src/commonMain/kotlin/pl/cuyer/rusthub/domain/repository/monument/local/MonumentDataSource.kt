package pl.cuyer.rusthub.domain.repository.monument.local

import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.model.Language

interface MonumentDataSource {
    suspend fun upsertMonuments(monuments: List<Monument>)
    suspend fun isEmpty(language: Language): Boolean
}
