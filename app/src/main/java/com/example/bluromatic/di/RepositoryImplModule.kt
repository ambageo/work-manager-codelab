package com.example.bluromatic.di

import android.content.Context
import com.example.bluromatic.data.WorkManagerBluromaticRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryImplModule {
    @Provides
    fun provideWorkManagerBluromaticRepositoryImpl(@ApplicationContext context: Context): WorkManagerBluromaticRepositoryImpl =
        WorkManagerBluromaticRepositoryImpl(context)
}