package com.example.architecturekotlin.data.repository.local

import com.example.architecturekotlin.data.db.WalkDao
import com.example.architecturekotlin.data.mapper.map
import com.example.architecturekotlin.domain.model.WalkModel
import com.example.architecturekotlin.domain.repository.local.WalkRepository
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.Pref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WalkRepositoryImpl @Inject constructor(
    private val walkDao : WalkDao,
    private val pref: Pref
) : WalkRepository {

    override suspend fun insertWalkCount(walkData: WalkModel) {
        walkDao.insert(walkData.map())
    }

    override suspend fun getWalkCount(): List<WalkModel> {
        return walkDao.getWalkCount().map { walkData ->
            walkData.map()
        }
    }

    override suspend fun deleteWalkCount(date: String) {

    }

    override suspend fun getTodayCountAsFlow(date: String): Flow<WalkModel> {
        return walkDao.getTodayCountAsFlow(date).map {
            it?.map() ?: WalkModel()
        }
    }


    override suspend fun getTodayCount(date: String, hour: String): WalkModel {
        return walkDao.getTodayCount(date)?.map() ?: WalkModel()
    }

    override suspend fun updateWalk(date: String, count: Int) {
        walkDao.updateCnt(count, date)
    }

    override suspend fun updateWalk2(walkData: WalkModel) {
        walkData.map()
    }

    override suspend fun upsertWalk(walkData: WalkModel) {
        walkDao.upsertCnt(walkData.map())
    }
}