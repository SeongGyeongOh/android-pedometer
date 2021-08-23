package com.example.architecturekotlin.presenter.main.walk_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.architecturekotlin.domain.usecase.GetWalkUseCase
import com.example.architecturekotlin.domain.usecase.SaveWalkUseCase
import com.example.architecturekotlin.util.common.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val saveWalkUseCase: SaveWalkUseCase,
    private val getWalkUseCase: GetWalkUseCase
) : ViewModel() {

    private val _walkIntent = MutableSharedFlow<WalkIntent>()
    val walkIntent: SharedFlow<WalkIntent> get() = _walkIntent

    private val _walkState = MutableStateFlow<WalkState>(WalkState.Idle)
    val walkState: StateFlow<WalkState> get() = _walkState

    init {
        handleIntent()
    }

    fun setIntent(intent: WalkIntent) = viewModelScope.launch {
        _walkIntent.emit(intent)
    }

    private fun handleIntent() = viewModelScope.launch {
        walkIntent.collect {
            when (it) {
                is WalkIntent.SaveData -> {
                    Logger.d("뷰모델 WalkIntent.SaveData")
                    saveWalkData(it.date, it.count)
                }
                WalkIntent.GetData -> {
                    getWalkData()
                }
            }
        }
    }

    private fun saveWalkData(date: String, count: Int)
    = viewModelScope.launch(Dispatchers.Default) {
        _walkState.value = try {
            WalkState.Success(saveWalkUseCase.buildUseCase(Pair(date, count)))
        } catch (e: Exception) {
            WalkState.Fail(Error("Walk 데이터 저장에 실패하였습니다.", e.cause))
        }
    }

    private fun getWalkData() = viewModelScope.launch(Dispatchers.Default) {
        _walkState.value = try {
            WalkState.Success(getWalkUseCase.buildUseCase())
        } catch (e: Exception) {
            WalkState.Fail(Error("Walk 데이터 호출에 실패하였습니다.", e.cause))
        }
    }
}