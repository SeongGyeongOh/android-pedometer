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
import com.example.architecturekotlin.util.common.*
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

        if (pref.getBoolVal("isServiceRunning")) {
            startServiceViaWorker()
        }

        setVisibility()

        binding.moveBtn.setOnClickListener {
            val action = WalkFragmentDirections.actionWalkFragmentToWalkGraphFragment()
            findNavController().navigate(action)
        }

        binding.startWalkBtn.setOnClickListener {
            checkPermission(Build.VERSION.SDK_INT)
        }

        binding.endWalkBtn.setOnClickListener {
            stopService()
        }

        handleState()

        viewModel.setIntent(WalkIntent.GetTodayData(
            date = System.currentTimeMillis().getCurrentDateWithYear())
        )
    }

    private fun handleState() {
        viewModel.walkState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is WalkState.TodayCount -> {
                    state.walkData.asLiveData().observe(viewLifecycleOwner) {
                        binding.walkFixText.text = "?????? ?????? ?????? :  ${it.count}"
                    }
                }
                is WalkState.Fail -> {
                    Logger.d("?????? ${state.error.message}")
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
                startService()
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
            startService()
        } else {
            Toast.makeText(requireContext(), "????????? ????????? ?????? ????????? ???????????? ?????????", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
    }

    private fun startService() {
        pref.setBoolValue("isServiceRunning", true)
        val intent = Intent(requireContext(), WalkService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(intent)
        } else {
            activity?.startService(intent)
        }

        setVisibility()
    }

    private fun startServiceViaWorker() {
        Logger.d("??????????????? - ????????? ????????? ????????????")

        val UNIQUE_WORK_NAME = "StartWalkServiceViaWorker"
        val workManager = WorkManager.getInstance(requireContext())

        val request = PeriodicWorkRequest.Builder(
            WalkWorker::class.java,
            16,
            TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun stopService() {
        pref.setBoolValue("isServiceRunning", false)
        setVisibility()

        val intent = Intent(requireContext(), WalkService::class.java)
        activity?.stopService(intent)
    }

    private fun setVisibility() {
        if (pref.getBoolVal("isServiceRunning")) {
            binding.startWalkBtn.visibility = GONE
            binding.walkFixText.visibility = VISIBLE
        } else {
            binding.startWalkBtn.visibility = VISIBLE
            binding.walkFixText.visibility = GONE
        }
    }
}