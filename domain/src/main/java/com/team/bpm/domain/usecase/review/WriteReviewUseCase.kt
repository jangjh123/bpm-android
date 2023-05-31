package com.team.bpm.domain.usecase.review

import com.team.bpm.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WriteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(
        studioId: Int,
        imageByteArrays: List<ByteArray>,
        rating: Double,
        recommends: List<String>,
        content: String
    ): Flow<Unit> {
        return reviewRepository.sendReview(
            studioId = studioId,
            imageByteArrays = imageByteArrays,
            rating = rating,
            recommends = recommends,
            content = content
        )
    }
}