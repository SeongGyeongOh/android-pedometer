package com.example.architecturekotlin.presenter.main.walk_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.example.architecturekotlin.databinding.FragmentWalkGraphBinding
import com.example.architecturekotlin.presenter.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import com.example.architecturekotlin.util.common.Logger


@AndroidEntryPoint
class WalkGraphFragment : BaseFragment<FragmentWalkGraphBinding>() {

    val viewModel: WalkViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWalkGraphBinding {
        return FragmentWalkGraphBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestIntent()
        handleState()
    }

    private fun requestIntent() {
        viewModel.setIntent(WalkIntent.GetData)
    }

    private fun handleState() {
        viewModel.walkState.asLiveData().observe(viewLifecycleOwner) { state ->
            when(state) {
                is WalkState.Success -> {
                    state.walkData.asLiveData().observe(viewLifecycleOwner) {
                        Logger.d("데이터 가져옴 ${it}")
                        binding.tvData.text = it.toString()
                    }
                }
                is WalkState.Fail -> {
                    Logger.d("데이터 못가져옴 ${state.error.message}")
                }
            }
        }
    }
}