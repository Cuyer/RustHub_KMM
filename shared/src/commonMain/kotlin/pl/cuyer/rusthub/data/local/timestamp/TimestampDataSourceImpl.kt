package pl.cuyer.rusthub.data.local.timestamp

import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion
import kotlinx.datetime.toInstant
import pl.cuyer.rusthub.common.Constants.DEFAULT_REFRESH_TIMEOUT
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.repository.timestamp.TimestampDataSource
import kotlin.time.Duration.Companion.minutes

class TimestampDataSourceImpl(
    db: RustHubDatabase
) : TimestampDataSource, Queries(db) {
    override fun insertTimestamp(timestamp: Instant) {
        queries.insertLastFetchTimestamp(
            last_fetch = timestamp.toString()
        )
    }

    override fun getOrInitTimestamp(): Instant {
        val existing = queries.getLastFetchTimestamp().executeAsOneOrNull()
        return if (existing == null) {
            val initialTimestamp = System.now().minus(DEFAULT_REFRESH_TIMEOUT.minutes)
            queries.insertLastFetchTimestamp(initialTimestamp.toString())
            initialTimestamp
        } else {
            existing.last_fetch?.let { Instant.parse(it) } ?: System.now()
                .minus(DEFAULT_REFRESH_TIMEOUT.minutes)
        }
    }


}