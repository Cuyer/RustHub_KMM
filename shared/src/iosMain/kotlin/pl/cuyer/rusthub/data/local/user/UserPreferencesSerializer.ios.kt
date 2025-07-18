package pl.cuyer.rusthub.data.local.user

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource
import pl.cuyer.rusthub.UserPreferencesProto

class UserPreferencesSerializer : OkioSerializer<UserPreferencesProto> {
    override val defaultValue: UserPreferencesProto = UserPreferencesProto()

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun readFrom(source: BufferedSource): UserPreferencesProto =
        try {
            ProtoBuf.decodeFromByteArray(
                UserPreferencesProto.serializer(),
                source.readByteArray()
            )
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun writeTo(t: UserPreferencesProto, sink: BufferedSink) {
        sink.write(
            ProtoBuf.encodeToByteArray(UserPreferencesProto.serializer(), t)
        )
    }
}
