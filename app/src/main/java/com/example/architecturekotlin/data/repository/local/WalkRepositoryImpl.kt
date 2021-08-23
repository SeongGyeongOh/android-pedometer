package com.example.architecturekotlin.data.repository.local

import com.example.architecturekotlin.data.db.WalkDao
import com.example.architecturekotlin.data.mapper.map
import com.example.architecturekotlin.domain.model.WalkModel
import com.example.architecturekotlin.domain.repository.local.WalkRepository
import kotlinx.coroutines.flow.Flow
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
}