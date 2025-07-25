package pl.cuyer.rusthub.util

expect object CrashReporter {
    fun log(message: String)
    fun recordException(throwable: Throwable)
    fun setUserId(userId: String?)
}
