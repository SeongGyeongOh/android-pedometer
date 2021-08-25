package com.example.architecturekotlin.util.common

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

}

fun Long.getCurrentDate(): String {
    val date = Date(this)
    return SimpleDateFormat("yyyy-MM-dd").format(date)
}