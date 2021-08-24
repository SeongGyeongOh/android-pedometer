package com.example.architecturekotlin.presenter.main.walk_fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.example.architecturekotlin.databinding.FragmentWalkBinding
import com.example.architecturekotlin.presenter.BaseFragment
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.Pref
import com.example.architecturekotlin.util.common.checkRuntimePermission
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class WalkFragment : BaseFragment<FragmentWalkBinding>(), SensorEventListener {

    @Inject
    lateinit var pref: Pref

    val viewModel: WalkViewModel by viewModels()

    var workManager: WorkManager? = null
    var simpleRequest: OneTimeWorkRequest? = null
    var sensorManager: SensorManager? = null
    var sensor: Sensor? = null
    var mSteps: Int = 0
    var sCounterSteps: Int = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWalkBinding {
        return FragmentWalkBinding.inflate(inflater, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.walkCount.observe(viewLifecycleOwner) {
            binding.walkText.text = it.toString()
        }

        binding.moveBtn.setOnClickListener {
            val action = WalkFragmentDirections.actionWalkFragmentToWalkGraphFragment()
            findNavController().navigate(action)
        }

        binding.startWalkBtn.setOnClickListener {
            checkRuntimePermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION,
                PackageManager.PERMISSION_GRANTED,
                action = { },
                askPermission = { permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION) })
        }

        checkRuntimePermission(
            requireContext(),
            Manifest.permission.ACTIVITY_RECOGNITION,
            PackageManager.PERMISSION_GRANTED,
            action = {
                binding.startWalkBtn.visibility = GONE
                binding.walkFixText.visibility = VISIBLE
                initSensor()
                setWorker() },
            askPermission = { })

        handleState()
    }

    private fun handleState() {
        viewModel.walkState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                WalkState.Counting -> {
                    if (sensor == null) {
                        Toast.makeText(requireContext(), "센서 없다 ㅡㅡ", Toast.LENGTH_SHORT).show()
                    } else {
                        sensorManager!!.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
                    }
                }

                is WalkState.Success -> {
                    state.walkData.asLiveData().observe(viewLifecycleOwner) { dataList ->
                        Logger.d("성공!! ${dataList}")
                    }
                }

                is WalkState.Fail -> {
                    Logger.d("실패 ${state.error.message}")
                }
            }
        }
    }

    private fun setWorker() {
        Logger.d("여기 실행됨!!!")

        /** WorkManager 객체 */
        workManager = WorkManager.getInstance(requireContext())

        /** request 객체 */
        simpleRequest =
            OneTimeWorkRequest.Builder(SaveWalkWorker::class.java)
                .build()

        /** work manager에 work request 추가 */
        workManager?.enqueue(simpleRequest!!)

        val workInfo = workManager?.getWorkInfoByIdLiveData(simpleRequest!!.id)

        workInfo?.let {
            handleWorkerState(it)
        }
    }

    private fun handleWorkerState(workInfo: LiveData<WorkInfo>) {
        workInfo.observe(viewLifecycleOwner) { info ->
            when (info.state) {
                WorkInfo.State.SUCCEEDED -> {
                    Logger.d("WorkInfoState : ${info.state.name}")
                    viewModel.setIntent(
                        WalkIntent.SaveData(Calendar.getInstance().timeInMillis.toString(), 1))
                }
                WorkInfo.State.FAILED -> { Logger.d("WorkInfoState : ${info.state.name}") }
                else -> { Logger.d("WorkInfoState : ${info.state.name}") }
            }
        }
    }

    private fun initSensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        viewModel.setIntent(WalkIntent.CountWalk)
    }

    private val permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initSensor()
            setWorker()
            binding.startWalkBtn.visibility = GONE
            binding.walkFixText.visibility = VISIBLE
        } else {
            Toast.makeText(requireContext(), "만보기 사용을 위해 권한을 허용해 주세요", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            Logger.d("만보기 - 카운트")

            if (sCounterSteps < 1) {
                sCounterSteps = event.values[0].toInt()
            }

            mSteps = event.values[0].toInt() - sCounterSteps
            viewModel.countSteps(mSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
}