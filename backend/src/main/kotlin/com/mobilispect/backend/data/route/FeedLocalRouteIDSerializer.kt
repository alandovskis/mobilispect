package com.mobilispect.backend.data.route

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A [KSerializer] that can handle [FeedLocalRouteID].
 */
object FeedLocalRouteIDSerializer : KSerializer<FeedLocalRouteID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("FeedLocalRouteID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): FeedLocalRouteID = FeedLocalRouteID(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: FeedLocalRouteID) {
        encoder.encodeString(value.str)
    }
}