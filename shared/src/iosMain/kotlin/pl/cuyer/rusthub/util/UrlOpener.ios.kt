package pl.cuyer.rusthub.util

actual class UrlOpener {
    actual fun openUrl(url: String) {
        // no-op for iOS
    }
}
