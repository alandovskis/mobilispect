package com.mobilispect.common.domain.time

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

class FormatTimeUseCase @Inject constructor() {
    private var locale: Locale

    init {
        locale = Locale.getDefault()
    }

    constructor(locale: Locale) : this() {
        this.locale = locale
    }

    operator fun invoke(dateTime: LocalDateTime): String =
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale).format(dateTime)
}
