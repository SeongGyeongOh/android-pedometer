package com.example.architecturekotlin.presenter.main.barcode_fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.architecturekotlin.databinding.FragmentBarcodeBinding
import com.example.architecturekotlin.presenter.BaseFragment
import com.example.architecturekotlin.util.common.Logger
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.sqrt

@AndroidEntryPoint
class BarcodeFragment : BaseFragment<FragmentBarcodeBinding>(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var currentAcceleration = 0f
    private var mShakeTime: Long = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBarcodeBinding {
        return FragmentBarcodeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(this, sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        currentAcceleration = SensorManager.GRAVITY_EARTH
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values?.get(0) ?: 0f
            val y = event.values?.get(1) ?: 0f
            val z = event.values?.get(2) ?: 0f

            val gravityX = x / SensorManager.GRAVITY_EARTH
            val gravityY = y / SensorManager.GRAVITY_EARTH
            val gravityZ = z / SensorManager.GRAVITY_EARTH

            currentAcceleration =
                sqrt((gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ).toDouble())
                    .toFloat()

            if (currentAcceleration > SHAKE_THRESHOLD_GRAVITY) {

                val curTime = System.currentTimeMillis()
                if (mShakeTime + SHAKE_SKIP_TIME > curTime) {
                    return
                }
                mShakeTime = curTime
                mShakeTime++

//                Toast.makeText(requireContext(), "흔들림 감지!!", Toast.LENGTH_SHORT).show()
                generateBarcode()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun generateBarcode() {
        val text = "20210903"
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.CODE_128, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)

            binding.barcodeImg.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Logger.e("바코드 생성 에러")
        }
    }

    companion object {
        private const val SHAKE_THRESHOLD_GRAVITY = 2.7f
        private const val SHAKE_SKIP_TIME = 500
    }
}