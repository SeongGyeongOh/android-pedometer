package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.Pref

class WalkWorker(
    val context: Context,
    parameters: WorkerParameters
) : Worker(context, parameters) {

    var pref = Pref(context)

    override fun doWork(): Result {
        Logger.d("워커 needWorker ${pref.getBoolVal("needWorker")}")
        if (!WalkService().isServiceRunning && pref.getBoolVal("needWorker")) {
            val intent = Intent(context, WalkService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
        return Result.success()
    }
}