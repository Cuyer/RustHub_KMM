package pl.cuyer.rusthub.util

/** Schedules periodic token refresh. */
expect class MessagingTokenScheduler {
    fun schedule()
}
