package com.example.architecturekotlin.domain.repository.local

import com.example.architecturekotlin.domain.model.WalkModel
import kotlinx.coroutines.flow.Flow

interface WalkRepository {
    suspend fun insertWalkCount(walkData: WalkModel)
    suspend fun getWalkCount(): Flow<List<WalkModel>>
    suspend fun deleteWalkCount(date: String)
}