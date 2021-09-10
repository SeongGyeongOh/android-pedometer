package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        Logger.d("워커2 실행")
        val intent = Intent(context, WalkService::class.java)
        intent.putExtra("isInit", true)
        intent.putExtra("isInitialStepSetup", false)
        context.startForegroundService(intent)

        repeatWorker()

        return Result.success()
    }

    private fun repeatWorker() {
        Logger.d("워커2의 repeat Worker 실행")
        val request = OneTimeWorkRequest.Builder(WalkWorker2::class.java)
            .setInitialDelay(1, TimeUnit.HOURS)
            .addTag(REPEAT_TAG)
            .build()

        WorkManager.getInstance(context)
            .enqueue(request)
    }

    val REPEAT_TAG = "REPEAT"
}