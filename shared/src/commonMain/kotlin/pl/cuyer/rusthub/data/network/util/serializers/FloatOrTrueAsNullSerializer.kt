package pl.cuyer.rusthub.data.network.util.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

object FlexibleFloatSerializer : KSerializer<Float?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleFloat", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Float? {
        val input = decoder as? JsonDecoder
            ?: error("FlexibleFloatSerializer only works with Json format")

        val element = input.decodeJsonElement()

        return when (element) {
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content.toFloatOrNull()
                    element.booleanOrNull != null -> null
                    element.floatOrNull != null -> element.float
                    element.intOrNull != null -> element.int.toFloat()
                    else -> null
                }
            }
            else -> null
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Float?) {
        if (value != null) encoder.encodeFloat(value)
        else encoder.encodeNull()
    }
}
