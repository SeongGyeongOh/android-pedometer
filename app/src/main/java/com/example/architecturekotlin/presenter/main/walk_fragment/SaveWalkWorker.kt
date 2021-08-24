package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.architecturekotlin.util.common.Logger
import javax.inject.Inject


class SaveWalkWorker @Inject constructor(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params){

    override fun doWork(): Result {
        Logger.d("워커 실행..........#######")

        return Result.success()
    }
}