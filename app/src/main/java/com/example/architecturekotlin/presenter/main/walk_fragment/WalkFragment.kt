package com.example.architecturekotlin.presenter.main.walk_fragment

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.core.content.ContextCompat
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
class WalkFragment : BaseFragment<FragmentWalkBinding>() {

    @Inject
    lateinit var pref: Pref

    val viewModel: WalkViewModel by viewModels()

    var workManager: WorkManager? = null
    var simpleRequest: OneTimeWorkRequest? = null
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

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED -> {
                binding.startWalkBtn.visibility = GONE
                binding.walkFixText.visibility = VISIBLE
            }
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

        handleState()
    }

    private fun handleState() {
        viewModel.walkState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                WalkState.Counting -> {

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


//    private fun setWorker() {
//        /** WorkManager 객체 */
//        workManager = WorkManager.getInstance(requireContext())
//
//        val data = mapOf("isFirstRun" to pref.getBoolVal("isFirstRun"))
//        val inputData = Data.Builder().putAll(data).build()
//
//        /** request 객체 */
//        simpleRequest =
//            OneTimeWorkRequest.Builder(SaveWalkWorker::class.java)
//                .setInputData(inputData)
//                .build()
//
//        /** work manager에 work request 추가 */
//        workManager?.beginWith(simpleRequest!!)?.enqueue()
//
//
//    }

    private val permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pref.setBoolValue("isFirstRun", true)
//            setWorker()
            val intent = Intent(requireContext(), WalkService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity?.startForegroundService(intent)
            } else {
                activity?.startService(intent)
            }
            binding.startWalkBtn.visibility = GONE
            binding.walkFixText.visibility = VISIBLE
        } else {
            Toast.makeText(requireContext(), "만보기 사용을 위해 권한을 허용해 주세요", Toast.LENGTH_SHORT).show()
        }
    }
}