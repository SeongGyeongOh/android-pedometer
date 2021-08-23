package com.example.architecturekotlin.presenter.main

import com.example.architecturekotlin.domain.model.CntModel
import com.example.architecturekotlin.domain.model.TodoModel

sealed class MainState {
    object Idle: MainState()

    sealed class Todos {
        data class Success(val todos: List<TodoModel>): MainState()
        data class Fail(val error: Error): MainState()
    }

    sealed class TodoDetail {
        data class Success(val todo: TodoModel): MainState()
        data class Fail(val error: Error): MainState()
    }

    sealed class SaveCnt {
        data class Success(val id: Long): MainState()
        data class Fail(val error: Error): MainState()
    }

    sealed class GetCnt {
        data class Success(val cnts: CntModel): MainState()
        data class Fail(val error: Error): MainState()
    }
}