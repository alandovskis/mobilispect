package com.mobilispect.common.domain.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

class FormatTimeUseCase @Inject constructor() {
    fun invoke(dateTime: LocalDateTime): String =
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(dateTime)
}
