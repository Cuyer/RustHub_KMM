package pl.cuyer.rusthub.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CancellationException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.SecureRandom
import android.util.Base64
import java.io.InputStream
import java.io.OutputStream
import java.io.File


actual class DatabasePassphraseProvider(private val context: Context) {
    companion object {
        private const val KEY_ALIAS = "RustHubSqlCipherKey"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private fun getOrCreateKey(): SecretKey {
        val existing = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existing != null) return existing
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(spec)
        return generator.generateKey()
    }

    private inner class PassphraseSerializer : Serializer<ByteArray> {
        override val defaultValue: ByteArray = ByteArray(0)

        override suspend fun readFrom(input: InputStream): ByteArray {
            val bytes = input.readBytes()
            if (bytes.isEmpty()) return ByteArray(0)
            val iv = bytes.copyOfRange(0, IV_SIZE)
            val cipherData = bytes.copyOfRange(IV_SIZE, bytes.size)
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
            return cipher.doFinal(cipherData)
        }

        override suspend fun writeTo(t: ByteArray, output: OutputStream) {
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            val iv = cipher.iv
            val encrypted = cipher.doFinal(t)
            output.write(iv + encrypted)
        }
    }

    private fun generatePassphrase(): ByteArray {
        val passphrase = ByteArray(32)
        SecureRandom().nextBytes(passphrase)
        return passphrase
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val dataStore: DataStore<ByteArray> = DataStoreFactory.create(
        serializer = PassphraseSerializer(),
        scope = scope,
        produceFile = { File(context.filesDir, "db_passphrase") }
    )

    actual suspend fun getPassphrase(): String {
        val stored = try {
            dataStore.data.first()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ByteArray(0)
        }
        if (stored.isNotEmpty()) {
            return Base64.encodeToString(stored, Base64.NO_WRAP)
        }
        val newPass = generatePassphrase()
        dataStore.updateData { newPass }
        return Base64.encodeToString(newPass, Base64.NO_WRAP)
    }
}
