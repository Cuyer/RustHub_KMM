package pl.cuyer.rusthub.presentation.model

import pl.cuyer.rusthub.domain.model.SearchQuery

fun SearchQuery.toUi(): SearchQueryUi {
    return SearchQueryUi(
        query = query,
        id = id,
        timestamp = timestamp
    )}