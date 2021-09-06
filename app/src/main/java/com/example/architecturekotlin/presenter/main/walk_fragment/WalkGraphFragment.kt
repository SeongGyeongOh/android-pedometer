package com.example.architecturekotlin.presenter.main.walk_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.example.architecturekotlin.R
import com.example.architecturekotlin.databinding.FragmentWalkGraphBinding
import com.example.architecturekotlin.domain.model.WalkModel
import com.example.architecturekotlin.presenter.BaseFragment
import com.example.architecturekotlin.util.common.Logger
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WalkGraphFragment : BaseFragment<FragmentWalkGraphBinding>() {

    @Inject lateinit var adapter: WalkAdapter

    val viewModel: WalkViewModel by viewModels()
    var barChart: BarChart? = null
    var cntList = listOf<WalkModel>()
    var xAxisTitle = mutableListOf<String>()
    val valueList = mutableListOf<Double>()
    val entries = mutableListOf<BarEntry>()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWalkGraphBinding {
        return FragmentWalkGraphBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barChart = binding.barChart
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
                    cntList = if (state.walkData.size <= 7) {
                        state.walkData
                    } else {
                        state.walkData.subList(state.walkData.size - 7, state.walkData.size)
                    }
                    adapter.submitList(state.walkData)
                    setChart()
                }
                is WalkState.Fail -> {
                    Logger.d("데이터 못가져옴 ${state.error.message}")
                }
            }
        }
    }

    private fun setChart() {
        cntList.forEach { data ->
            valueList.add(data.count.toDouble())
            xAxisTitle.add(data.date)
        }

        valueList.forEachIndexed { index, data ->
            val barEntry = BarEntry(index.toFloat(), data.toFloat())
            entries.add(barEntry)
        }

        val barDataSet = BarDataSet(entries, "걸음 수")
        val data = BarData(barDataSet)

        initBarDataSet(barDataSet)
        setBarChart(data)
    }

    private fun initBarDataSet(barDataSet: BarDataSet) {
        barDataSet.apply {
            color = requireContext().getColor(R.color.chart_color)
            formSize = 15f
            setDrawValues(false)
            valueTextSize = 16f
        }
    }

    private fun setBarChart(data: BarData) {
        //우측 하단 그래프에 대한 설명 숨김
        val description = Description()
        description.isEnabled = false
        barChart?.description = description

        //확대 못하게
       barChart?.apply {
            setScaleEnabled(false)
            isDoubleTapToZoomEnabled = false
            setTouchEnabled(false)
        }

        //x축 세팅
        barChart?.xAxis?.apply {
            labelCount = valueList.size
            valueFormatter = IndexAxisValueFormatter(xAxisTitle)
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }

        //좌측 하단 bar에 대한 설명
        barChart?.legend?.apply {
            form = Legend.LegendForm.LINE
            textSize = 14f
        }

        barChart?.data = data
        barChart?.invalidate()
    }

    override fun onPause() {
        super.onPause()
        Logger.d("그래프 화면 onPause")
        cntList = listOf()
    }
}
