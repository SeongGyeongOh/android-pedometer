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
import javax.inject.Inject


@AndroidEntryPoint
class WalkGraphFragment : BaseFragment<FragmentWalkGraphBinding>() {

    @Inject lateinit var adapter: WalkAdapter

    val viewModel: WalkViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWalkGraphBinding {
        return FragmentWalkGraphBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.walkRecycler.adapter = adapter
        requestIntent()
        handleState()
    }

    private fun requestIntent() {
        viewModel.setIntent(WalkIntent.GetData)
    }

    private fun handleState() {
        viewModel.walkState.asLiveData().observe(viewLifecycleOwner) { state ->
            when(state) {
                is WalkState.TotalCount -> {
                    state.walkData.asLiveData().observe(viewLifecycleOwner) {
                        Logger.d("데이터 가져옴 ${it}")
                        adapter.submitList(it)
                    }
                }
                is WalkState.Fail -> {
                    Logger.d("데이터 못가져옴 ${state.error.message}")
                }
            }
        }
    }
}