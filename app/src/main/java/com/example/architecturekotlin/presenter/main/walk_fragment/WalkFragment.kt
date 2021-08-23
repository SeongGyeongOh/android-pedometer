package com.example.architecturekotlin.presenter.main.walk_fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.bumptech.glide.load.resource.SimpleResource
import com.example.architecturekotlin.databinding.FragmentWalkBinding
import com.example.architecturekotlin.presenter.BaseFragment
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.checkRuntimePermission
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class WalkFragment : BaseFragment<FragmentWalkBinding>() {

    val viewModel: WalkViewModel by viewModels()

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

        binding.moveBtn.setOnClickListener {
            val action = WalkFragmentDirections.actionWalkFragmentToWalkGraphFragment()
            findNavController().navigate(action)
        }

        checkRuntimePermission(
            requireContext(),
            Manifest.permission.ACTIVITY_RECOGNITION,
            PackageManager.PERMISSION_GRANTED,
            action = { initSensor() },
            askPermission = { permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION) })

        observeWorker()

        handleState()

    }

    private fun handleState() {
        viewModel.walkState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
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

    fun observeWorker() {
        /**
         * request 객체
         */
//        val simpleRequest = PeriodicWorkRequestBuilder<SaveWalkWorker>(15, TimeUnit.MINUTES)
//            .build()
//        val req = OneTimeWorkRequest.Builder(SaveWalkWorker::class.java).build()

        val aa = PeriodicWorkRequest.Builder(
            SaveWalkWorker::class.java, 16, TimeUnit.MINUTES
        ).build()

        /**
         * WorkManager 객체
         */
        val workManager = WorkManager.getInstance(requireContext())

        binding.saveWalkBtn.setOnClickListener {

            /**
             * work manager에 work request 추가
             */
            workManager.enqueue(aa)
//            workManager.beginWith(req).enqueue()
        }

        val workInfo = workManager.getWorkInfoByIdLiveData(aa.id)
//        val workInfo = workManager.getWorkInfoByIdLiveData(req.id)

        handleWorkerState(workInfo)
    }

    fun handleWorkerState(workInfo: LiveData<WorkInfo>) {
        workInfo.observe(viewLifecycleOwner) { info ->
            binding.walkText.text = info.state.name

            val workFinished = info.state.isFinished

            when (info.state) {
                WorkInfo.State.SUCCEEDED -> {
                    Logger.d("찍힘")
                    viewModel.setIntent(WalkIntent.SaveData("시간", 10))
                }
                WorkInfo.State.FAILED -> {
                    binding.walkText.text = "실패쓰 ${info.state}, ${info.state.name},$workFinished"
                }
                else -> {
                    binding.walkText.text = info.state.name
                }
            }
        }
    }

    private fun initSensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    private val permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initSensor()
        } else {
            Toast.makeText(requireContext(), "만보기 사용을 위해 권한을 허용해 주세요", Toast.LENGTH_SHORT).show()
        }
    }
}