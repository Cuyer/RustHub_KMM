package pl.cuyer.rusthub.util

expect class DeleteAccountScheduler {
    fun schedule(username: String, password: String)
}
