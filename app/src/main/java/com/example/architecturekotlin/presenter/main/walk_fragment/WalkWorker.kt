package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.architecturekotlin.util.common.Logger

class WalkWorker(
    val context: Context,
    parameters: WorkerParameters
) : Worker(context, parameters) {

    override fun doWork(): Result {
        if (!WalkService().isServiceRunning) {
            Logger.d("starting service from doWork")
            val intent = Intent(context, WalkService::class.java)

            /*
             * startForegroundService is similar to startService but with an implicit promise
             * that the service will call startForeground once it begins running.
             * The service is given an amount of time comparable to the ANR interval to do this,
             * otherwise the system will automatically stop the service and declare the app ANR.
             */
            //this.context.startService(intent);
            ContextCompat.startForegroundService(context, intent)
        }
        return Result.success()
    }
}