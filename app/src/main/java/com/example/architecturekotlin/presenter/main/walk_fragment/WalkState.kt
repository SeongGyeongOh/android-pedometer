package com.example.architecturekotlin.presenter.main.walk_fragment

import com.example.architecturekotlin.domain.model.WalkModel
import kotlinx.coroutines.flow.Flow

sealed class WalkState {
    object Idle : WalkState()
    object Counting : WalkState()
    data class TotalCount(val walkData: List<WalkModel>) : WalkState()
    data class TodayCount(val walkData: Flow<WalkModel>) : WalkState()
    data class Fail(val error: Error) : WalkState()
}