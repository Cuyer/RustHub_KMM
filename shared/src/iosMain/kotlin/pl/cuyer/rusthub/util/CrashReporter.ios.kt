package pl.cuyer.rusthub.util

actual object CrashReporter {
    actual fun log(message: String) {}

    actual fun recordException(throwable: Throwable) {}

    actual fun setUserId(userId: String?) {}
}
