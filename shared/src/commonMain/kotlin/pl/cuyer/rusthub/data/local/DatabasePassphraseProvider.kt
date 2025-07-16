package pl.cuyer.rusthub.data.local

expect class DatabasePassphraseProvider {
    suspend fun getPassphrase(): String
}
