package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import javax.inject.Inject


class SaveWalkWorker @Inject constructor(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val number = 10
        val result = number * number

        SystemClock.sleep(3000)

        Log.d("SimpleWorker", "SimpleWorker finished: $result")

        return Result.success()
    }
}