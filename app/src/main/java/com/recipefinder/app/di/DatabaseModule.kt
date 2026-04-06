package com.recipefinder.app.di

import android.content.Context
import androidx.room.Room
import com.recipefinder.app.core.constants.AppConstants
import com.recipefinder.app.data.local.dao.FavoriteDao
import com.recipefinder.app.data.local.dao.RecipeDao
import com.recipefinder.app.data.local.database.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRecipeDatabase(
        @ApplicationContext context: Context
    ): RecipeDatabase =
        Room.databaseBuilder(
            context,
            RecipeDatabase::class.java,
            AppConstants.DATABASE_NAME,
        )
            // In production, replace this with a proper Migration object.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    @Singleton
    fun provideRecipeDao(db: RecipeDatabase): RecipeDao =
        db.recipeDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(db: RecipeDatabase): FavoriteDao =
        db.favoriteDao()
}
