package com.mobilispect.mobile.domain.time

import kotlinx.datetime.format.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class FormatTimeUseCase(private val timeZone: TimeZone) {
    operator fun invoke(dateTime: LocalDateTime): String =
        dateTime.format(LocalDateTime.Formats.ISO)
       // DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale).format(dateTime)
}
