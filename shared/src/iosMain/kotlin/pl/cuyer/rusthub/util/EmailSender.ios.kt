package pl.cuyer.rusthub.util

actual class EmailSender {
    actual fun sendEmail(recipient: String, subject: String) {
        // no-op for iOS
    }
}
