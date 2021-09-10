package com.example.architecturekotlin.presenter.main.walk_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architecturekotlin.domain.usecase.GetTodayWalkUseCase
import com.example.architecturekotlin.domain.usecase.GetWalkUseCase
import com.example.architecturekotlin.util.common.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val getWalkUseCase: GetWalkUseCase,
    private val getTodayWalkUseCase: GetTodayWalkUseCase,
) : ViewModel() {

    private val _walkIntent = MutableSharedFlow<WalkIntent>()
    val walkIntent: SharedFlow<WalkIntent> get() = _walkIntent

    private val _walkState = MutableStateFlow<WalkState>(WalkState.Idle)
    val walkState: StateFlow<WalkState> get() = _walkState

    private val _walkCount = MutableLiveData<Int>(0)
    val walkCount: LiveData<Int> get() = _walkCount

    init {
        handleIntent()
    }

    fun setIntent(intent: WalkIntent) = viewModelScope.launch {
        _walkIntent.emit(intent)
    }

    fun countSteps(count: Int) {
        _walkCount.value = count
    }

    private fun handleIntent() = viewModelScope.launch {
        walkIntent.collect {
            when (it) {
                WalkIntent.CountWalk -> {
                    _walkState.value = WalkState.Counting
                }
                WalkIntent.GetData -> {
                    getWalkData()
                }
                is WalkIntent.GetTodayData -> {
                    getTodayData(it.date)
                }
            }
        }
    }

    private fun getWalkData() = viewModelScope.launch(Dispatchers.Default) {
        _walkState.value = try {
            WalkState.TotalCount(getWalkUseCase.buildUseCase())
        } catch (e: Exception) {
            WalkState.Fail(Error("Walk 데이터 호출에 실패하였습니다.", e.cause))
        }
    }

    private fun getTodayData(date: String) = viewModelScope.launch(Dispatchers.Default) {
        _walkState.value = try {
            WalkState.TodayCount(getTodayWalkUseCase.buildUseCase(date))
        } catch (e: Exception) {
            WalkState.Fail(Error("오늘의 Walk 데이터 호출에 실패하였습니다.", e.cause))
        }
    }
}