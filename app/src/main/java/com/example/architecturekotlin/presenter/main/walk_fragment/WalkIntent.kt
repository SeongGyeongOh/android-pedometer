package com.example.architecturekotlin.presenter.main.walk_fragment

sealed class WalkIntent {
    object CountWalk : WalkIntent()
    object GetData : WalkIntent()
    data class GetTodayData(val date: String) : WalkIntent()
}