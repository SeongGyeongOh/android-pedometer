package com.example.architecturekotlin.util.common

import java.text.SimpleDateFormat
import java.util.*

fun Long.getCurrentDateWithYear(): String {
    val date = Date(this)
    return SimpleDateFormat("yyyy-MM-dd HH").format(date)
}

fun Long.getCurrentYear(): String {
    val date = Date(this)
    return SimpleDateFormat("yyyy").format(date)
}

fun Long.getCurrentDate(): String {
    val date = Date(this)
    return SimpleDateFormat("MM-dd").format(date)
}

fun Long.getCurrentTime(): String {
    val date = Date(this)
    return SimpleDateFormat("HH:mm:ss").format(date)
}

