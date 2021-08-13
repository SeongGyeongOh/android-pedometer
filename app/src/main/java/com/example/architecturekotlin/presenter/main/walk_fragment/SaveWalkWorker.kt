package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class SaveWalkWorker (
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val number = 10
        val result = number * number

        Log.d("SimpleWorker", "SimpleWorker finished: $result")

        return Result.success()
    }
}

