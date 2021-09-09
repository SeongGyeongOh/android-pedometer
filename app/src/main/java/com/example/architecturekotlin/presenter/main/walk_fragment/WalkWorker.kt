package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.Pref
import java.util.*
import java.util.concurrent.TimeUnit

class WalkWorker(
    val context: Context,
    parameters: WorkerParameters
) : Worker(context, parameters) {

    var pref = Pref(context)

    override fun doWork(): Result {
        Logger.d("워커1 실행")
        Logger.d("워커 isServiceRunning ${pref.getBoolVal("isServiceRunning")} \n needWorker ${pref.getBoolVal("needWorker")}")
        if (pref.getBoolVal("isServiceRunning") && pref.getBoolVal("needWorker")) {
            val intent = Intent(context, WalkService::class.java)
            intent.putExtra("isInit", false)
            intent.putExtra("isReboot", true)
            ContextCompat.startForegroundService(context, intent)

            WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)


            setWorkerTime()
        }
        return Result.success()
    }

    private fun setWorkerTime() {
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY, 24)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)
        date.set(Calendar.MILLISECOND, 0)

        val duration = date.timeInMillis - System.currentTimeMillis()
        Logger.d("기간 확인 $duration")
        Logger.d("기간 확인 현재 ${System.currentTimeMillis()}")

        val workManager = WorkManager.getInstance(context)

        val request = OneTimeWorkRequest.Builder(WalkWorker2::class.java)
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .build()

        workManager
            .enqueueUniqueWork(
                "init step",
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

    val UNIQUE_WORK_NAME = "StartWalkServiceViaWorker"
    val WORK_TAG = "StartServiceInFragment"
}