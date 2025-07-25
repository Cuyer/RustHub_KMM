package pl.cuyer.rusthub.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual object CrashReporter {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    actual fun log(message: String) {
        crashlytics.log(message)
    }

    actual fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    actual fun setUserId(userId: String?) {
        crashlytics.setUserId(userId ?: "")
    }
}
