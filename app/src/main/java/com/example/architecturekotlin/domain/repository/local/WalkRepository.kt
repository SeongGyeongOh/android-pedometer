package com.example.architecturekotlin.domain.repository.local

import com.example.architecturekotlin.domain.model.WalkModel
import kotlinx.coroutines.flow.Flow

interface WalkRepository {
    suspend fun insertWalkCount(walkData: WalkModel)
    suspend fun getWalkCount(): List<WalkModel>
    suspend fun deleteWalkCount(date: String)
    suspend fun getTodayCountAsFlow(date: String): Flow<WalkModel>
    suspend fun getTodayCount(date: String, hour: String): WalkModel?
    suspend fun updateWalk(date: String, count: Int)
    suspend fun updateWalk2(walkData: WalkModel)
    suspend fun upsertWalk(walkData: WalkModel)
}