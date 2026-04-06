package com.recipefinder.app.data.mapper

import com.recipefinder.app.data.local.entity.RecipeEntity
import com.recipefinder.app.data.remote.dto.RecipeDto
import com.recipefinder.app.domain.model.Difficulty
import com.recipefinder.app.domain.model.Recipe

// ─── Extension functions keep mappers purely functional with zero state ───────

// DTO → Domain
fun RecipeDto.toDomain(isFavorite: Boolean = false): Recipe = Recipe(
    id              = id,
    title           = title,
    imageUrl        = image,
    cookTimeMinutes = readyInMinutes,
    servings        = servings,
    likes           = likes,
    difficulty      = Difficulty.fromLabel(difficulty),
    category        = dishTypes.firstOrNull() ?: "general",
    ingredients     = extendedIngredients.map { it.original.ifBlank { "${it.amount} ${it.unit} ${it.name}".trim() } },
    instructions    = analyzedInstructions
                        .flatMap { it.steps }
                        .sortedBy { it.number }
                        .map { it.step },
    isFavorite      = isFavorite,
    rating          = rating / 10.0,        // Spoonacular uses 0-100 scale
    authorName      = authorName,
    authorAvatar    = authorAvatar,
    calories        = calories,
)

// DTO → Entity
fun RecipeDto.toEntity(isFavorite: Boolean = false): RecipeEntity = RecipeEntity(
    id              = id,
    title           = title,
    imageUrl        = image,
    cookTimeMinutes = readyInMinutes,
    servings        = servings,
    likes           = likes,
    difficulty      = difficulty,
    category        = dishTypes.firstOrNull() ?: "general",
    ingredients     = extendedIngredients.map { it.original.ifBlank { "${it.amount} ${it.unit} ${it.name}".trim() } },
    instructions    = analyzedInstructions
                        .flatMap { it.steps }
                        .sortedBy { it.number }
                        .map { it.step },
    isFavorite      = isFavorite,
    rating          = rating / 10.0,
    authorName      = authorName,
    authorAvatar    = authorAvatar,
    calories        = calories,
)

// Entity → Domain
fun RecipeEntity.toDomain(): Recipe = Recipe(
    id              = id,
    title           = title,
    imageUrl        = imageUrl,
    cookTimeMinutes = cookTimeMinutes,
    servings        = servings,
    likes           = likes,
    difficulty      = Difficulty.fromLabel(difficulty),
    category        = category,
    ingredients     = ingredients,
    instructions    = instructions,
    isFavorite      = isFavorite,
    rating          = rating,
    authorName      = authorName,
    authorAvatar    = authorAvatar,
    calories        = calories,
)

// Domain → Entity  (used when persisting a favourite)
fun Recipe.toEntity(): RecipeEntity = RecipeEntity(
    id              = id,
    title           = title,
    imageUrl        = imageUrl,
    cookTimeMinutes = cookTimeMinutes,
    servings        = servings,
    likes           = likes,
    difficulty      = difficulty.label,
    category        = category,
    ingredients     = ingredients,
    instructions    = instructions,
    isFavorite      = isFavorite,
    rating          = rating,
    authorName      = authorName,
    authorAvatar    = authorAvatar,
    calories        = calories,
)
