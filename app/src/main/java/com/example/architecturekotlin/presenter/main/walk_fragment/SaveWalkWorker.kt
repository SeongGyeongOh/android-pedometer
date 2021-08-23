package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.core.content.contentValuesOf
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.architecturekotlin.data.db.WalkDatabase
import com.example.architecturekotlin.data.entity.WalkEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SaveWalkWorker @Inject constructor(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
//        val number = 10
//        val result = number * number
//
//        Log.d("워커", "SimpleWorker finished: $result")

        //OneTimeRequest를 사용할 때, 다음번 실행되는 시간을 준비하자
//        val curDate = Calendar.getInstance()
//        val dueDate = Calendar.getInstance()

        // Set Execution around 00:00:00 AM - TODO
//        dueDate.set(Calendar.HOUR_OF_DAY, 11)
//        dueDate.set(Calendar.MINUTE, 30)
//        dueDate.set(Calendar.SATURDAY, 0)
//
//        if (dueDate.before(curDate)) {
//            dueDate.add(Calendar.HOUR_OF_DAY, 24)
//        }
//
//        val timeDiff = dueDate.timeInMillis - curDate.timeInMillis

        val dailyWorkReq = OneTimeWorkRequestBuilder<SaveWalkWorker>()
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context)
            .enqueue(dailyWorkReq)

        insertData(context)

        return Result.success()
    }

    private fun insertData(context: Context) = CoroutineScope(Dispatchers.Default).launch {
        WalkDatabase.getDatabase(context).walkDao().insert(WalkEntity(1,"ㅁㄴㅇㄹ", 0))
    }
}