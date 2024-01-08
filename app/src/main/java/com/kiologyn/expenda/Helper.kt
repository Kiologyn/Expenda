package com.kiologyn.expenda

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class Helper {
    companion object {
        const val TIME_FORMAT = "HH:mm"

        const val DATE_FORMAT = "dd.MM.yyyy"
        const val DATE_MY_FORMAT = "MMM yyyy"
        const val DATE_DMY_FORMAT = "dd MMM yyyy"

        const val DATETIME_FORMAT = "$TIME_FORMAT $DATE_FORMAT"

        const val SHARED_PREFERENCES_SETTINGS_NAME = "settings"

        const val ROUND_DECIMAL_PLACES = 2
        const val CATEGORIES_MAX_LENGTH = 25
        const val HOME_SCREEN_RECORDS_AMOUNT = 10
    }
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
}
fun LocalDateTime.toSeconds(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().epochSecond
}
fun LocalDateTime.adjustToNearestDay(): LocalDateTime {
    return LocalDateTime.of(
        toLocalDate(),
        LocalTime.MIDNIGHT,
    ).plusDays(if (toLocalTime().isAfter(LocalTime.NOON)) 1 else 0)
}
fun LocalDateTime.formatTime(): String {
    return this.format(DateTimeFormatter.ofPattern(Helper.TIME_FORMAT))
}
fun LocalDateTime.formatDate(): String {
    return this.format(DateTimeFormatter.ofPattern(Helper.DATE_FORMAT))
}
fun LocalDateTime.formatDateMY(): String {
    return this.format(DateTimeFormatter.ofPattern(Helper.DATE_MY_FORMAT))
}
fun LocalDateTime.formatDateDMY(): String {
    return this.format(DateTimeFormatter.ofPattern(Helper.DATE_DMY_FORMAT))
}
fun LocalDateTime.formatDateTime(): String {
    return this.format(DateTimeFormatter.ofPattern(Helper.DATETIME_FORMAT))
}
