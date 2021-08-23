package com.example.architecturekotlin.presenter.main.walk_fragment

import com.example.architecturekotlin.domain.model.WalkModel

sealed class WalkIntent {
    data class SaveData(val date: String, val count: Int) : WalkIntent()
    object GetData : WalkIntent()
    object Idle : WalkIntent()
}