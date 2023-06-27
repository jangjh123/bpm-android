package com.team.bpm.domain.usecase.splash

import com.team.bpm.domain.repository.SplashRepository
import dagger.Reusable
import javax.inject.Inject

@Reusable
class GetUserTokenUseCase @Inject constructor(private val splashRepository: SplashRepository) {
    operator fun invoke(): String {
        return splashRepository.getUserToken()
    }
}