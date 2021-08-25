package com.example.architecturekotlin.domain.usecase

import com.example.architecturekotlin.domain.model.WalkModel
import com.example.architecturekotlin.domain.repository.local.WalkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayWalkUseCase @Inject constructor(
    private val repository: WalkRepository
) : UseCaseWithParams<Flow<List<WalkModel>>, String>() {

    public override suspend fun buildUseCase(date: String): Flow<List<WalkModel>> {
        return repository.getTodayCount(date)
    }
}