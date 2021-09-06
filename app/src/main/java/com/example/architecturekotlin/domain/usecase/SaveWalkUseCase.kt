package com.example.architecturekotlin.domain.usecase

import com.example.architecturekotlin.domain.model.WalkModel
import com.example.architecturekotlin.domain.repository.local.WalkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//class SaveWalkUseCase @Inject constructor(
//    private val walkRepository: WalkRepository
//) : UseCaseWithParams<List<WalkModel>, Pair<String, Int>>(){
//
//    public override suspend fun buildUseCase(params: Pair<String, Int>): Flow<List<WalkModel>> {
//        walkRepository.insertWalkCount(
//            WalkModel(
//                date = params.first,
//                count = params.second)
//        )
//
//        return walkRepository.getWalkCount()
//    }
//}