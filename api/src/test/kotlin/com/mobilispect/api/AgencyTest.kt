package com.mobilispect.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AgencyTest {
    @Test
    fun blankIDThrowsException() {
        assertThrows<IllegalStateException> { Agency(id = NonBlankString(""), name = NonBlankString("A")) }
    }

    @Test
    fun nonBlankIDIsOK() {
        val id = NonBlankString("a")

        val subject = Agency(id = id, name = NonBlankString("A"))

        assertEquals(subject.id, id)
    }

    @Test
    fun blankNameThrowsException() {
        assertThrows<IllegalStateException> { Agency(id = NonBlankString("a"), name = NonBlankString("")) }
    }
}
