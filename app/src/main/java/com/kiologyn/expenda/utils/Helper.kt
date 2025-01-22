package com.kiologyn.expenda.utils

import android.content.Context
import android.widget.Toast
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow


class Helper {
    companion object {
        const val TIME_FORMAT = "HH:mm"

        const val DATE_FORMAT = "dd.MM.yyyy"
        const val DATE_MY_FORMAT = "MMM yyyy"
        const val DATE_DMY_FORMAT = "dd MMM yyyy"

        const val DATETIME_FORMAT = "$TIME_FORMAT $DATE_FORMAT"

        const val ROUND_DECIMAL_PLACES = 2
        const val CATEGORIES_MAX_LENGTH = 25
        const val HOME_SCREEN_RECORDS_AMOUNT = 10

        fun showToast(context: Context, message: String) =
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

// ZonedDateTime
fun Long.toZonedDateTime(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
    return Instant.ofEpochSecond(this).atZone(zone)
}

// LocalDate/Time format
fun LocalDate.format(format: String): String {
    return this.format(DateTimeFormatter.ofPattern(format))
}
fun LocalDateTime.format(format: String): String {
    return this.format(DateTimeFormatter.ofPattern(format))
}

// LocalDateTime
fun Long.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return this.toZonedDateTime(zone).toLocalDateTime()
}
fun LocalDateTime.toSeconds(zone: ZoneId = ZoneId.systemDefault()): Long {
    return this.atZone(zone).toEpochSecond()
}

fun LocalDateTime.adjustToNearestDay(): LocalDateTime {
    return LocalDateTime.of(
        this.toLocalDate(),
        LocalTime.MIDNIGHT,
    ).plusDays(this.toLocalTime().isAfter(LocalTime.NOON).toInt().toLong())
}

// LocalDate
fun Long.toLocalDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate {
    return this.toZonedDateTime(zone).toLocalDate()
}
fun LocalDate.toSeconds(zone: ZoneId = ZoneId.systemDefault()): Long {
    return this.atStartOfDay(zone).toEpochSecond()
}

// others
fun Double.round(precision: Int = Helper.ROUND_DECIMAL_PLACES): Double {
    val precisionValue = 10.0.pow(precision.toDouble())
    return (this * precisionValue).toInt() / precisionValue
}

fun Boolean.toInt(): Int = if (this) 1 else 0
