package pl.cuyer.rusthub.domain.repository.timestamp

import kotlinx.datetime.Instant

interface TimestampDataSource {
    fun insertTimestamp(timestamp: Instant)
    fun getOrInitTimestamp(): Instant
}