package com.mobilispect.common.domain.time

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime


internal class FormatTimeUseCaseTest {
    val subject = FormatTimeUseCase()

    @Test
    fun givenValidDateInEvening() {
        val actual = subject(LocalDateTime.of(2022, 7, 20, 23, 59))
        assertThat(actual).isEqualTo("11:59 p.m.")
    }

    @Test
    fun givenValidDateInMorning() {
        val actual = subject(LocalDateTime.of(2022, 7, 20, 11, 59))
        assertThat(actual).isEqualTo("11:59 a.m.")
    }

    @Test
    fun saturatesAtMinimum() {
        val actual = subject(LocalDateTime.MIN)
        assertThat(actual).isEqualTo("12:00 a.m.")
    }

    @Test
    fun saturatesAtMaximum() {
        val actual = subject(LocalDateTime.MAX)
        assertThat(actual).isEqualTo("11:59 p.m.")
    }

}