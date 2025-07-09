package pl.cuyer.rusthub.data.network.util.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

//TODO illegal input null
@OptIn(ExperimentalTime::class)
object InstantSerializer : KSerializer<Instant?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("kotlin.time.Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant?) {
        if (value != null) {
            encoder.encodeString(value.toString())
        } else {
            encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: Decoder): Instant? {
        val input = decoder as? JsonDecoder
            ?: error("InstantSerializer only works with Json format")

        val element = input.decodeJsonElement()

        return when (element) {
            is JsonPrimitive -> element.contentOrNull?.let { Instant.parse(it) }
            else -> null
        }
    }}
