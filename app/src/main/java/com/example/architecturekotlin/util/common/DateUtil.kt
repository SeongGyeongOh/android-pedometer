package com.example.architecturekotlin.util.common

import java.text.SimpleDateFormat
import java.util.*

fun Long.getCurrentDate(): String {
    val date = Date(this)
    return SimpleDateFormat("yyyy-MM-dd : HH").format(date)
}

fun Long.getCurrentHour(): String {
    val date = Date(this)
    return SimpleDateFormat("HH").format(date)
}

