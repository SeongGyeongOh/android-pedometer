package com.example.architecturekotlin.presenter.main.walk_fragment

sealed class WalkIntent {
    object CountWalk : WalkIntent()
    data class SaveData(val date: String, val count: Int) : WalkIntent()
    object GetData : WalkIntent()
    data class GetTodayData(val date: String) : WalkIntent()
    object Idle : WalkIntent()
}