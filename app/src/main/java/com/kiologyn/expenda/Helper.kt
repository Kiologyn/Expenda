package com.kiologyn.expenda

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Helper {
    companion object {
        const val TIME_FORMAT: String = "HH:mm"
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT)

        const val DATE_FORMAT: String = "dd.MM.yyyy"
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)


        const val DATABASE_NAME = "database"

        const val SHARED_PREFERENCES_SETTINGS_NAME = "settings"
    }
}

fun LocalDateTime.toMilliseconds(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}
fun LocalDateTime.formatTime(): String {
    return this.format(Helper.timeFormatter)
}
fun LocalDateTime.formatDate(): String {
    return this.format(Helper.dateFormatter)
}
