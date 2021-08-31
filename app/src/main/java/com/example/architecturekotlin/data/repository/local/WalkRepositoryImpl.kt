package com.example.architecturekotlin.data.repository.local

import com.example.architecturekotlin.data.db.WalkDao
import com.example.architecturekotlin.data.mapper.map
import com.example.architecturekotlin.domain.model.WalkModel
import com.example.architecturekotlin.domain.repository.local.WalkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WalkRepositoryImpl @Inject constructor(
    private val walkDao : WalkDao
) : WalkRepository {

    override suspend fun insertWalkCount(walkData: WalkModel) {
        walkDao.insert(walkData.map())
    }

    override suspend fun getWalkCount(): Flow<List<WalkModel>> {
        return walkDao.getWalkCount().map { walkList ->
            walkList.map {
                it.map()
            }
        }
    }

    override suspend fun deleteWalkCount(date: String) {

    }

    override suspend fun getTodayCountAsFlow(date: String): Flow<WalkModel> {
        return walkDao.getTodayCountAsFlow(date).map {
            it?.map() ?: WalkModel()
        }
    }

    override suspend fun getTodayCount(date: String): WalkModel {
        return walkDao.getTodayCount(date)?.map() ?: WalkModel()
    }

    override suspend fun updateWalk(date: String, count: Int) {
        walkDao.updateCnt(count, date)
    }

    override suspend fun upsertWalk(walkData: WalkModel) {
        walkDao.upsertCnt(walkData.map())
    }
}