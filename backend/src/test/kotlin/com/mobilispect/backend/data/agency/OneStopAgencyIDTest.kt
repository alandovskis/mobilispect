package com.mobilispect.backend.data.agency

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class OneStopAgencyIDTest {
    @Test
    fun agencyNotCreatedIfIDMissingGeoHash() {
        assertThrows<IllegalArgumentException> {
            OneStopAgencyID("o--a")
        }
    }

    @Test
    fun agencyNotCreatedIfIDMissingName() {
        assertThrows<IllegalArgumentException> {
            OneStopAgencyID("o-a-")
        }
    }

    @Test
    fun idIsAcceptedIfHasGeohashAndName() {
        OneStopAgencyID("o-a-b")
    }

    @Test
    fun idIsAcceptedIfMissingGeohashButHasName() {
        OneStopAgencyID("o-orleans~express~ca")
    }
}
