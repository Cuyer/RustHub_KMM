package pl.cuyer.rusthub.util

expect class EmailSender {
    fun sendEmail(recipient: String, subject: String)
}
