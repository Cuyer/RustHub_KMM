package pl.cuyer.rusthub.presentation.model

import pl.cuyer.rusthub.domain.model.SearchQuery
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun SearchQuery.toUi(): SearchQueryUi {
    return SearchQueryUi(
        query = query,
        id = id,
        timestamp = timestamp
    )
}