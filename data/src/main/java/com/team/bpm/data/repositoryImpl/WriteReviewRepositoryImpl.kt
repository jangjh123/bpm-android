package com.team.bpm.data.repositoryImpl

import com.team.bpm.data.network.BPMResponseHandlerV2
import com.team.bpm.data.network.MainApi
import com.team.bpm.data.util.convertByteArrayToWebpFile
import com.team.bpm.data.util.createImageMultipartBody
import com.team.bpm.domain.repository.WriteReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WriteReviewRepositoryImpl @Inject constructor(
    private val mainApi: MainApi
) : WriteReviewRepository {
    override suspend fun sendReview(
        studioId: Int,
        imageByteArrays: List<ByteArray>,
        rating: Double,
        recommends: List<String>,
        content: String
    ): Flow<Unit> {
        return flow {
            BPMResponseHandlerV2().handle {
                mainApi.sendReview(
                    studioId = 1,
                    files = imageByteArrays.map { imageByteArray ->
                        createImageMultipartBody(
                            key = "file",
                            file = convertByteArrayToWebpFile(imageByteArray)
                        )
                    },
                    rating = rating,
                    recommends = recommends,
                    content = content
                )
            }.collect {
                emit(Unit)
            }
        }
    }
}