package com.recipefinder.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── List response wrapper ───────────────────────────────────────────────────

data class RecipeListResponseDto(
    @SerializedName("results")  val results: List<RecipeDto> = emptyList(),
    @SerializedName("total")    val total: Int               = 0,
    @SerializedName("page")     val page: Int                = 1,
)

// ─── Single recipe DTO ───────────────────────────────────────────────────────

data class RecipeDto(
    @SerializedName("id")           val id: Int,
    @SerializedName("title")        val title: String,
    @SerializedName("image")        val image: String         = "",
    @SerializedName("readyInMinutes") val readyInMinutes: Int = 0,
    @SerializedName("servings")     val servings: Int         = 2,
    @SerializedName("aggregateLikes") val likes: Int          = 0,
    @SerializedName("difficulty")   val difficulty: String    = "Easy",
    @SerializedName("dishTypes")    val dishTypes: List<String> = emptyList(),
    @SerializedName("extendedIngredients") val extendedIngredients: List<IngredientDto> = emptyList(),
    @SerializedName("analyzedInstructions") val analyzedInstructions: List<InstructionDto> = emptyList(),
    @SerializedName("spoonacularScore") val rating: Double    = 0.0,
    @SerializedName("authorName")   val authorName: String    = "",
    @SerializedName("authorAvatar") val authorAvatar: String  = "",
    @SerializedName("calories")     val calories: Int         = 0,
)

data class IngredientDto(
    @SerializedName("original") val original: String = "",
    @SerializedName("name")     val name: String     = "",
    @SerializedName("amount")   val amount: Double   = 0.0,
    @SerializedName("unit")     val unit: String     = "",
)

data class InstructionDto(
    @SerializedName("steps") val steps: List<StepDto> = emptyList(),
)

data class StepDto(
    @SerializedName("number") val number: Int    = 0,
    @SerializedName("step")   val step: String   = "",
)
