package com.example.architecturekotlin.presenter.main.walk_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.work.*
import com.example.architecturekotlin.databinding.FragmentWalkBinding
import com.example.architecturekotlin.domain.model.TodoModel
import com.example.architecturekotlin.presenter.BaseFragment
import java.util.concurrent.TimeUnit

class WalkFragment : BaseFragment<FragmentWalkBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWalkBinding {
        return FragmentWalkBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val todo = TodoModel(1, "asdf")

        val inputData = Data.Builder()
            .putAll(mapOf(Pair("ㅁㄴㅇㄹ", "ㅁㄴㅇㄹ")))
            .build()

        //request 객체
        val simpleRequest = PeriodicWorkRequestBuilder<SaveWalkWorker>(15, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()

//        val req = OneTimeWorkRequest.Builder(SaveWalkWorker::class.java).build()

        //WorkManager 객체
        val workManager = WorkManager.getInstance(requireContext())

        binding.saveWalkBtn.setOnClickListener {
            //work manager에 work request 추가
            workManager
                .enqueue(simpleRequest)
        }

        val workInfo = workManager.getWorkInfoByIdLiveData(simpleRequest.id)

        workInfo.observe(viewLifecycleOwner) { info ->
            val workFinished = info.state.isFinished

            when (info.state) {
                WorkInfo.State.SUCCEEDED -> {
                    Toast.makeText(requireContext(), "성공", Toast.LENGTH_SHORT).show()
                    binding.walkText.text = "성공성공성공쓰"
                }
                WorkInfo.State.FAILED -> {
                    binding.walkText.text = "실패쓰 ${info.state}, ${info.state.name}, $workFinished"
                }
                else -> {
                    binding.walkText.text = info.state.name
                }
            }
        }
    }
}