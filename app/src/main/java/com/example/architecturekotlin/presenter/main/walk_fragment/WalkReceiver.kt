package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.architecturekotlin.util.common.Logger

class MyReceiver : BroadcastReceiver() {
    private val TAG = "MyReceiver"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        Logger.d("브로드캐스트 리시버 - onReceive")


        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Logger.d("브로드캐스트 리시버 - ACTION_BOOT_COMPLETED")

                val workManager = WorkManager.getInstance(context)
                val startServiceRequest = OneTimeWorkRequest.Builder(WalkWorker::class.java)
                    .build()
                workManager.enqueue(startServiceRequest)
            }
        }
    }
}