package com.example.architecturekotlin.presenter.main.walk_fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.architecturekotlin.databinding.FragmentWalkBinding
import com.example.architecturekotlin.presenter.BaseFragment
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.Pref
import com.example.architecturekotlin.util.common.getCurrentDate
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class WalkFragment @Inject constructor() : BaseFragment<FragmentWalkBinding>() {

    @Inject
    lateinit var pref: Pref

    val viewModel: WalkViewModel by viewModels()

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

        checkPermission(Build.VERSION.SDK_INT)

        binding.moveBtn.setOnClickListener {
            val action = WalkFragmentDirections.actionWalkFragmentToWalkGraphFragment()
            findNavController().navigate(action)
        }

        binding.startWalkBtn.setOnClickListener {
            checkPermission(Build.VERSION.SDK_INT)
        }

        handleState()

        viewModel.setIntent(WalkIntent.GetTodayData(date = System.currentTimeMillis().getCurrentDate()))
    }

    private fun handleState() {
        viewModel.walkState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                WalkState.Counting -> {

                }
                is WalkState.TodayCount -> {
                    state.walkData.asLiveData().observe(viewLifecycleOwner) {
                        binding.walkFixText.text = "오늘 걸은 걸음 :  ${it.count}"
                    }
                }

                is WalkState.TotalCount -> {
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermission(sdk: Int) {
        val permission = if (sdk >= 29) {
            Manifest.permission.ACTIVITY_RECOGNITION
        } else {
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) -> {
                startServiceViaWorker()
                binding.startWalkBtn.visibility = GONE
                binding.walkFixText.visibility = VISIBLE
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    private val permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pref.setBoolValue("isFirstRun", true)
            startService()
            binding.startWalkBtn.visibility = GONE
            binding.walkFixText.visibility = VISIBLE
        } else {
            Toast.makeText(requireContext(), "만보기 사용을 위해 권한을 허용해 주세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startService() {
        if (!WalkService().isServiceRunning) {
            val intent = Intent(requireContext(), WalkService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity?.startForegroundService(intent)
            } else {
                activity?.startService(intent)
            }
        }
    }

    private fun startServiceViaWorker() {
        Logger.d("startServiceViaWorker called")
        val UNIQUE_WORK_NAME = "StartWalkServiceViaWorker"
        val workManager = WorkManager.getInstance(requireContext())

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes (
        // same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        val request = PeriodicWorkRequest.Builder(
            WalkWorker::class.java,
            16,
            TimeUnit.MINUTES
        ).build()

        // below method will schedule a new work, each time app is opened
        //workManager.enqueue(request);

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // https://developer.android.com/topic/libraries/architecture/workmanager/how-to/unique-work
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}