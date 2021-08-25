package com.example.architecturekotlin.presenter.main.walk_fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.work.*
import com.example.architecturekotlin.data.db.WalkDatabase
import com.example.architecturekotlin.data.entity.WalkEntity
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.getCurrentDate
import kotlinx.coroutines.*
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

        insertData(context, System.currentTimeMillis().getCurrentDate(), 1)

        return Result.success()
    }

    private val sensorListener = object : SensorEventListener {
        /** 센서로부터 측정된 값이 전달되는 메소드 */
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                Logger.d("센서 실행 - 만보기 카운트")
                val workManager = WorkManager.getInstance(context)

                val simpleRequest =
                    OneTimeWorkRequestBuilder<SaveWalkWorker>()
                        .build()

                workManager.beginWith(simpleRequest).enqueue()
            }
        }

        /** 센서의 정확도가 변경되면 실행되는 메소드(거의 사용 안됨) */
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