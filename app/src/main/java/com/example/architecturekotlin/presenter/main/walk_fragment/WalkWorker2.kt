package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.Pref
import java.util.concurrent.TimeUnit

class WalkWorker2 constructor(
    val context: Context,
    parameters: WorkerParameters
) : Worker(context, parameters) {

    var pref = Pref(context)

    override fun doWork(): Result {
        Logger.d("워커2 실행")
        WalkService().isInit = true
        val intent = Intent(context, WalkService::class.java)
        intent.putExtra("isInit", true)
        ContextCompat.startForegroundService(context, intent)

        repeatWorker()

        return Result.success()
    }

    fun repeatWorker() {
        val request = PeriodicWorkRequest.Builder(
            WalkWorker2::class.java,
            24,
            TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    val UNIQUE_WORK_NAME = "StartWalkServiceViaWorker"
}