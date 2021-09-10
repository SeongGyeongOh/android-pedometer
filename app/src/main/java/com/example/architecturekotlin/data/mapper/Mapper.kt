package com.example.architecturekotlin.data.mapper

import com.example.architecturekotlin.data.entity.CntEntity
import com.example.architecturekotlin.data.entity.TodoEntity
import com.example.architecturekotlin.data.entity.WalkEntity
import com.example.architecturekotlin.domain.model.CntModel
import com.example.architecturekotlin.domain.model.TodoModel
import com.example.architecturekotlin.domain.model.WalkModel


fun CntModel.map() = CntEntity(
    id = 0,
    cnt = count
)

fun CntEntity.map() = CntModel(
    count = cnt
)

fun TodoEntity.map() = TodoModel(
    id = id,
    title = title
)

fun WalkEntity.map() = WalkModel(
    id = id,
    date = date,
    hour = hour,
    count = count
)

fun WalkModel.map() = WalkEntity(
    id = id,
    date = date,
    hour = hour,
    count = count
)