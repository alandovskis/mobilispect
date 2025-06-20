package com.mobilispect.backend.schedule.route

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [KSerializer] that can handle [OneStopRouteIDSerializer].
 */
object OneStopRouteIDSerializer : KSerializer<OneStopRouteID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OneStopRouteID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OneStopRouteID = OneStopRouteID(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: OneStopRouteID) {
        encoder.encodeString(value.str)
    }
}
