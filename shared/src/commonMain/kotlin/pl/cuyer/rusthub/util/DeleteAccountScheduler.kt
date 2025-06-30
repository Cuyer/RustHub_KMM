package pl.cuyer.rusthub.util

expect class DeleteAccountScheduler {
    fun schedule(password: String)
}
