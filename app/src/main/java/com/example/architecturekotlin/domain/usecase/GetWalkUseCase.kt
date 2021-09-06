package com.example.architecturekotlin.domain.usecase

import com.example.architecturekotlin.domain.model.WalkModel
import com.example.architecturekotlin.domain.repository.local.WalkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWalkUseCase @Inject constructor(
    private val repository: WalkRepository
) : UseCaseWithoutParams<List<WalkModel>>() {

    public override suspend fun buildUseCase(): List<WalkModel> {
        return repository.getWalkCount()
    }
}