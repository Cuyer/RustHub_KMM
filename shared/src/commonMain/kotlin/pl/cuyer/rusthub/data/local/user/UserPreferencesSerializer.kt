package pl.cuyer.rusthub.data.local.user

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

class UserPreferencesSerializer : Serializer<UserPreferencesProto> {
    override val defaultValue: UserPreferencesProto = UserPreferencesProto()

    override suspend fun readFrom(input: InputStream): UserPreferencesProto =
        try {
            ProtoBuf.decodeFromByteArray(UserPreferencesProto.serializer(), input.readBytes())
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: UserPreferencesProto, output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(UserPreferencesProto.serializer(), t))
    }
}
