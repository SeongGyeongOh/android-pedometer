package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.architecturekotlin.data.db.WalkDatabase
import com.example.architecturekotlin.data.entity.WalkEntity
import com.example.architecturekotlin.util.common.DateUtil
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.getCurrentTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class SaveWalkWorker @Inject constructor(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params){

    @Inject
    lateinit var walkFragment: WalkFragment

    private var sensor: Sensor? = null

    override fun doWork(): Result {

        Logger.d("워커 실행..........#######")

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (inputData.getBoolean("isFirstRun", false)) {

            if (sensor == null) {
                Toast.makeText(context, "실행할 수 있는 센서가 없습니다", Toast.LENGTH_SHORT).show()
            } else {
                sensorManager.registerListener(
                    sensorListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }

        insertData(context, System.currentTimeMillis().getCurrentTime(), 1)

        return Result.success()
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {

                val workManager = WorkManager.getInstance(context)

                val simpleRequest =
                    OneTimeWorkRequestBuilder<SaveWalkWorker>()
                        .build()

                workManager.beginWith(simpleRequest).enqueue()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {   }
    }

    private fun insertData(
        context: Context,
        date: String,
        count: Int
    ) = CoroutineScope(Dispatchers.Default).launch {
        Logger.d("워커에서 데이터 추가하기")
        WalkDatabase.getDatabase(context).walkDao().insert(WalkEntity(date = date, count = count))
    }
}