package com.example.note.presentation.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


object DateFormatter {

    private val millisInHour = TimeUnit.HOURS.toMillis(1)
    private val millisInDay = TimeUnit.DAYS.toMillis(1)

    private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

    fun formateCurrentDate(): String {
        return formatter.format(System.currentTimeMillis())

    }

    fun formatDateFromTimeMillis(timestamp: Long): String {
        return formatter.format(timestamp)
    }

    fun formateDateToString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < millisInHour -> "Только что"
            diff < millisInDay -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "$hours часов назад"
            }
            else -> {
                formatter.format(timestamp)
            }
        }
    }
}