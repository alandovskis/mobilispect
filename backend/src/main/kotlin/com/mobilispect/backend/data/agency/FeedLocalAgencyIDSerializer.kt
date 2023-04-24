package com.mobilispect.backend.data.agency

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [KSerializer] that can handle [FeedLocalAgencyID].
 */
object FeedLocalAgencyIDSerializer : KSerializer<FeedLocalAgencyID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("FeedLocalAgencyID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): FeedLocalAgencyID = FeedLocalAgencyID(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: FeedLocalAgencyID) {
        encoder.encodeString(value.str)
    }
}
