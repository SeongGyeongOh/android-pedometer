package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class MyReceiver : BroadcastReceiver() {
    private val TAG = "MyReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called")

        //ContextCompat.startForegroundService(context, new Intent(context, MyService.class));

        // We are starting MyService via a worker and not directly because since Android 7
        // (but officially since Lollipop!), any process called by a BroadcastReceiver
        // (only manifest-declared receiver) is run at low priority and hence eventually
        // killed by Android. Docs: https://developer.android.com/guide/components/broadcasts#effects-process-state

        val workManager = WorkManager.getInstance(context)
        val startServiceRequest = OneTimeWorkRequest.Builder(WalkWorker::class.java)
            .build()
        workManager.enqueue(startServiceRequest)
    }
}
