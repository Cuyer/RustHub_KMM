package pl.cuyer.rusthub.util

import platform.Foundation.NSError
import CrashReporterBridge

actual object CrashReporter {
    actual fun log(message: String) {
        CrashReporterBridge.log(message)
    }

    actual fun recordException(throwable: Throwable) {
        val error = NSError(domain = "CrashReporter", code = 0, userInfo = mapOf<Any?, Any?>("message" to (throwable.message ?: "")))
        CrashReporterBridge.recordError(error)
    }

    actual fun setUserId(userId: String?) {
        CrashReporterBridge.setUserId(userId)
    }
}
