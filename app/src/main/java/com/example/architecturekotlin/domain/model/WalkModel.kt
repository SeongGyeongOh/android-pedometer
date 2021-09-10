package com.example.architecturekotlin.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WalkModel (
    val id: Int = 0,
    val date: String = "",
    val hour: String = "",
    val count: Int = 0
)