package com.example.bluromatic.di

import com.example.bluromatic.data.BluromaticRepository
import com.example.bluromatic.data.BluromaticRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepository(impl: BluromaticRepositoryImpl): BluromaticRepository
}