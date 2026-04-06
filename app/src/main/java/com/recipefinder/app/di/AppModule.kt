package com.recipefinder.app.di

import com.recipefinder.app.domain.usecase.FilterRecipesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides application-scoped objects that don't belong in more specific modules.
 * Use cases with @Inject constructors are provided automatically by Hilt;
 * only stateless helpers or use-cases needing custom setup live here.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * [FilterRecipesUseCase] is a pure function with no dependencies – Hilt
     * would auto-provide it via @Inject constructor, but we declare it here
     * explicitly as a singleton to avoid repeated allocation.
     */
    @Provides
    @Singleton
    fun provideFilterRecipesUseCase(): FilterRecipesUseCase = FilterRecipesUseCase()
}
