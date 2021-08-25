package com.example.architecturekotlin.presenter.main.walk_fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.architecturekotlin.databinding.FragmentWalkBinding
import com.example.architecturekotlin.presenter.BaseFragment
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.Pref
import com.example.architecturekotlin.util.common.checkRuntimePermission
import com.example.architecturekotlin.util.common.getCurrentDate
import dagger.hilt.android.AndroidEntryPoint
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

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startService()
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
        viewModel.setIntent(WalkIntent.GetTodayData(date = System.currentTimeMillis().getCurrentDate()))
    }

    private fun handleState() {
        viewModel.walkState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                WalkState.Counting -> {

                }
                is WalkState.TodayCount -> {
                    state.walkData.asLiveData().observe(viewLifecycleOwner) { dataList ->
                        binding.walkFixText.text = "오늘 걸은 걸음 :  ${dataList.size}"
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
        val intent = Intent(requireContext(), WalkService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(intent)
        } else {
            activity?.startService(intent)
        }
    }
}