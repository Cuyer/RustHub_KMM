package pl.cuyer.rusthub.util

import platform.UIKit.UIPasteboard

actual class ClipboardHandler {
    actual fun copyToClipboard(label: String, text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}