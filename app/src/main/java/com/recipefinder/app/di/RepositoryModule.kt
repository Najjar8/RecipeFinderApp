package com.recipefinder.app.di

import com.recipefinder.app.data.repository.RecipeRepositoryImpl
import com.recipefinder.app.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the domain interface [RecipeRepository] to its concrete
 * implementation [RecipeRepositoryImpl]. Using @Binds (vs @Provides)
 * generates less code in Hilt's generated component.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        impl: RecipeRepositoryImpl
    ): RecipeRepository
}
