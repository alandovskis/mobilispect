package com.mobilispect.backend.data.agency

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [KSerializer] that can handle [OneStopAgencyID].
 */
object OneStopAgencyIDSerializer : KSerializer<OneStopAgencyID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OneStopAgencyID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): OneStopAgencyID = OneStopAgencyID(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: OneStopAgencyID) {
        encoder.encodeString(value.str)
    }
}
