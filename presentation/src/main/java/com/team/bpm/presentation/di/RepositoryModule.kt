package com.team.bpm.presentation.di

import com.team.bpm.data.datastore.DataStoreManager
import com.team.bpm.data.network.MainApi
import com.team.bpm.data.repositoryImpl.*
import com.team.bpm.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideMainRepository(
        mainApi: MainApi
    ): MainRepository {
        return MainRepositoryImpl(mainApi)
    }

    @Singleton
    @Provides
    fun provideSplashRepository(
        dataStoreManager: DataStoreManager,
        mainApi: MainApi
    ): SplashRepository {
        return SplashRepositoryImpl(dataStoreManager, mainApi)
    }

    @Singleton
    @Provides
    fun provideSignUpRepository(
        mainApi: MainApi
    ): SignUpRepository {
        return SignUpRepositoryImpl(mainApi)
    }

    @Singleton
    @Provides
    fun provideScheduleRepository(
        mainApi: MainApi
    ): ScheduleRepository {
        return ScheduleRepositoryImpl(mainApi)
    }

    @Singleton
    @Provides
    fun provideStudioDetailRepository(
        mainApi: MainApi
    ): StudioDetailRepository {
        return StudioDetailRepositoryImpl(mainApi)
    }

    @Singleton
    @Provides
    fun provideReviewRepository(
        mainApi: MainApi
    ): ReviewRepository {
        return ReviewRepositoryImpl(mainApi)
    }

    @Singleton
    @Provides
    fun provideWriteReviewRepository(
        mainApi: MainApi
    ): WriteReviewRepository {
        return WriteReviewRepositoryImpl(mainApi)
    }

    @Singleton
    @Provides
    fun provideSearchStudioRepository(
        mainApi: MainApi
    ): SearchStudioRepository {
        return SearchStudioRepositoryImpl(mainApi)
    }

    @Singleton
    @Provides
    fun provideRegisterStudioRepository(
        mainApi: MainApi
    ): RegisterStudioRepository {
        return RegisterStudioRepositoryImpl(mainApi)
    }
}