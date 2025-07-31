package pl.cuyer.rusthub.presentation.di

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import pl.cuyer.rusthub.UserPreferencesProto
import java.io.InputStream
import java.io.OutputStream

class UserPreferencesSerializer : Serializer<UserPreferencesProto> {
    override val defaultValue: UserPreferencesProto = UserPreferencesProto()

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(input: InputStream): UserPreferencesProto =
        try {
            ProtoBuf.decodeFromByteArray(UserPreferencesProto.serializer(), input.readBytes())
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            throw CorruptionException("Cannot read proto.", exception)
        }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(t: UserPreferencesProto, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(UserPreferencesProto.serializer(), t))
    }
}
