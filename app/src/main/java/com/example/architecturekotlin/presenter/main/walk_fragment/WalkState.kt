package com.example.architecturekotlin.presenter.main.walk_fragment

import com.example.architecturekotlin.domain.model.WalkModel
import kotlinx.coroutines.flow.Flow

sealed class WalkState {
    object Idle : WalkState()
    object Counting : WalkState()
    data class Success(val walkData: Flow<List<WalkModel>>) : WalkState()
    data class Fail(val error: Error): WalkState()
}