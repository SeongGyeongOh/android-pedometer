package com.example.architecturekotlin.util.common

import android.content.Context
import androidx.core.content.ContextCompat

fun checkRuntimePermission(context: Context,
                           permission: String,
                           isGranted: Int,
                           action: () -> Unit,
                           askPermission: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(context, permission) == isGranted -> {
            action()
        } else -> {
            askPermission()
        }
    }
}