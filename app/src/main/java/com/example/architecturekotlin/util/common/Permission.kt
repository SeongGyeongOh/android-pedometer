package com.example.architecturekotlin.util.common

import android.content.Context
import androidx.core.content.ContextCompat

fun checkRuntimePermission(context: Context,
                           permission: String,
                           isGranted: Int,
                           action: () -> Unit,
                           askPermission: () -> Unit) {
    when (isGranted) {
        ContextCompat.checkSelfPermission(context, permission) -> {
            action()
        }
        else -> {
            askPermission()
        }
    }
}