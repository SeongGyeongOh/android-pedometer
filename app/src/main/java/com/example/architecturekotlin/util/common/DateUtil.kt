package com.example.architecturekotlin.util.common

import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

}

fun Long.getCurrentTime(): String {
    val date = Date(this)
    return SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date)
}