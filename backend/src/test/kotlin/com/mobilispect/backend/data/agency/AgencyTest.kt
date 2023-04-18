package com.mobilispect.backend.data.agency

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AgencyTest {
    @Test
    fun agencyNotCreatedIfIDMissingGeoHash() {
        assertThrows<IllegalArgumentException> {
            Agency(_id = "o--a", name = "name", version = "version")
        }
    }

    @Test
    fun agencyNotCreatedIfIDMissingName() {
        assertThrows<IllegalArgumentException> {
            Agency(_id = "o-a-", name = "name", version = "version")
        }
    }

    @Test
    fun agencyIsCreatedIfValidID() {
        Agency(_id = "o-a-b", name = "name", version = "version")
    }
}
