package com.example.architecturekotlin.presenter.main.walk_fragment

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.architecturekotlin.R
import com.example.architecturekotlin.domain.model.WalkModel
import com.example.architecturekotlin.domain.repository.local.WalkRepository
import com.example.architecturekotlin.presenter.main.MainActivity
import com.example.architecturekotlin.util.common.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WalkService @Inject constructor(): Service() {

    @Inject
    lateinit var walkRepository: WalkRepository

    @Inject
    lateinit var pref: Pref

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private var noti: Notification? = null
    private var sCounterSteps: Int? = null
    private var defaultStep: Int = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d("서비스 - onStartCommand")
        pref.setBoolValue("isServiceRunning", true)
        pref.setBoolValue("needWorker", true)

        /** 포그라운드 서비스 돌리기 */
        /** 아래 notification을 띄우지 않는 경우 앱이 죽음
         * android.app.RemoteServiceException: Context.startForegroundService() did not then call Service.startForeground() */
        createNotificationChannel()

        val intent1 = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 0, intent1, 0)

        noti = NotificationCompat.Builder(this, "ChannelId1")
            .setContentTitle("만보기 테스트")
            .setContentText("만보기 돌아가는중")
            .setSmallIcon(R.drawable.icon_walk)
            .setContentIntent(pIntent)
            .build()

        startForeground(1, noti)

        sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (sensor == null) {
            Toast.makeText(this, "실행할 수 있는 센서가 없습니다", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(
                sensorListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        // check android version - over OREO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ChannelId1",
                "Foreground notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.vibrationPattern = longArrayOf(0)
            channel.enableVibration(true)
            channel.setSound(null, null)

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        Logger.d("서비스 - onDestroy")
        pref.setIntValue("addedVal", defaultStep)

        if (pref.getBoolVal("isServiceRunning")) {
            Logger.d("서비스 - 혼자 죽음")

            val intent = Intent(this, MyReceiver::class.java)
            intent.action = "ACTION_RESTART"
            sendBroadcast(intent)
        } else {
            pref.setBoolValue("needWorker", false)
        }

        stopForeground(true)
        sensorManager?.unregisterListener(sensorListener)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val sensorListener = object : SensorEventListener {
        /** 센서로부터 측정된 값이 전달되는 메소드 */
        override fun onSensorChanged(event: SensorEvent?) {
            val today = System.currentTimeMillis().getCurrentDateWithYear()
            val yesterday = pref.getValue("yesterday")
            val defaultVal = pref.getIntValue("addedVal")

            CoroutineScope(Dispatchers.Default).launch {
                Logger.d("오늘과 어제를 비교해보자\n오늘: ${today}, 어제: $yesterday")
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                    if (yesterday != today) {
                        sCounterSteps = event.values[0].toInt()
                        pref.setValue("yesterday", today)
                    } else if (sCounterSteps == null) {
                        sCounterSteps = event.values[0].toInt() - defaultVal
                    }

                    val addedVal = event.values[0].toInt() - sCounterSteps!!
                    defaultStep = addedVal
                    Logger.d("디비에 추가되는 데이터 ${addedVal} : ${event.values[0].toInt()} : $sCounterSteps")

                    insertData(today, addedVal)
                }
            }
        }

        /** 센서의 정확도가 변경되면 실행되는 메소드(거의 사용 안됨) */
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {   }
    }

    private fun insertData(
        date: String,
        addedVal: Int
    ) = CoroutineScope(Dispatchers.Default).launch {

        walkRepository.upsertWalk(WalkModel(date = date, count = addedVal))
    }
}