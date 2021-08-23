package com.example.architecturekotlin.presenter.main.main_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.architecturekotlin.R
import com.example.architecturekotlin.databinding.FragmentMainBinding
import com.example.architecturekotlin.presenter.BaseFragment
import com.example.architecturekotlin.presenter.main.MainIntent
import com.example.architecturekotlin.presenter.main.MainViewModel
import com.example.architecturekotlin.util.common.Logger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.presenter = this
    }

    fun goCntFragment() {
        val action = MainFragmentDirections.actionMainFragmentToCntFragment()
        findNavController().navigate(action)
    }

    fun goTodoFragment() {
        val action = MainFragmentDirections.actionMainFragmentToTodoFragment()
        findNavController().navigate(action)
    }

    fun goWalkFragment() {
        val action = MainFragmentDirections.actionMainFragmentToWalkFragment()
        findNavController().navigate(action)
    }

    fun goBarcodeFragment() {
        val action = MainFragmentDirections.actionMainFragmentToBarcodeFragment()
        findNavController().navigate(action)
    }
}
