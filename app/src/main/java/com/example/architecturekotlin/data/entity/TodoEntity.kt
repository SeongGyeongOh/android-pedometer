package com.example.architecturekotlin.data.entity

import com.example.architecturekotlin.domain.model.TodoModel
import kotlinx.serialization.Serializable

@Serializable
data class TodoEntity(
    val userId: Int = -1,
    val id: Int = -1,
    val title: String = "",
    val completed: Boolean = false
) {

    fun titleMapper(): TodoModel {
        return TodoModel(title = title)
    }
}
