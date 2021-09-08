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
class WalkService @Inject constructor(): Service(), SensorEventListener {

    @Inject
    lateinit var walkRepository: WalkRepository

    @Inject
    lateinit var pref: Pref

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private var noti: Notification? = null
    var defaultVal: Int = 0
    var isInit: Boolean = false
    var sCounterSteps: Int = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d("서비스 - onStartCommand")
        pref.setBoolValue("isServiceRunning", true)
        pref.setBoolValue("needWorker", true)
        val isInit = intent?.getBooleanExtra("isInit", false)

        if (isInit != true) {
            sCounterSteps = pref.getIntValue("defaultStep")
        }


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
            try {
                sensorManager?.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            } catch (e: Exception) {
                Logger.e("서비스 - 리스너 실패 ${e.cause} : ${e.message}")
            }
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
        pref.setIntValue("defaultStep", defaultVal)

        if (pref.getBoolVal("isServiceRunning")) {
            Logger.d("서비스 - 혼자 죽음")

            val intent = Intent(this, MyReceiver::class.java)
            intent.action = "ACTION_RESTART"
            sendBroadcast(intent)
        } else {
            pref.setBoolValue("needWorker", false)
        }

        stopForeground(true)
        sensorManager?.unregisterListener(this)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val today = System.currentTimeMillis().getCurrentDateWithYear()
        CoroutineScope(Dispatchers.IO).launch {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                if (sCounterSteps < 1) {
                    sCounterSteps = event.values[0].toInt()
                    isInit = false
                }

                val addedVal = event.values[0].toInt() - sCounterSteps
                defaultVal = sCounterSteps
                Logger.d("디비에 추가되는 데이터 ${addedVal} : ${event.values[0].toInt()} : $sCounterSteps")

                insertData(today, addedVal)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun insertData(
        date: String,
        addedVal: Int
    ) = CoroutineScope(Dispatchers.IO).launch {
        walkRepository.upsertWalk(WalkModel(date = date, count = addedVal))
    }
}