package pl.cuyer.rusthub.util

expect class ClipboardHandler {
    fun copyToClipboard(label: String, text: String)
}