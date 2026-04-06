package com.recipefinder.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

/**
 * OkHttp [Interceptor] that short-circuits real network calls and returns
 * pre-baked JSON responses. This allows the app to work out-of-the-box
 * without a Spoonacular API key.
 *
 * Replace this interceptor with a real [ApiKeyInterceptor] (or remove it
 * entirely) once you have a valid API key.
 */
class MockInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val url  = chain.request().url.toString()
        val body = when {
            url.contains("/complexSearch") && url.contains("query=") ->
                buildSearchResponse(extractQuery(url))
            url.contains("/complexSearch") ->
                RECIPE_LIST_JSON
            url.contains("/information") ->
                RECIPE_DETAIL_JSON
            else ->
                """{"error":"not_found"}"""
        }

        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun extractQuery(url: String): String =
        url.substringAfter("query=").substringBefore("&").lowercase()

    private fun buildSearchResponse(query: String): String {
        val filtered = MOCK_RECIPES.filter {
            it.title.lowercase().contains(query) ||
                    it.category.lowercase().contains(query)
        }
        val items = filtered.joinToString(",") { buildRecipeJson(it) }
        return """{"results":[$items],"total":${filtered.size},"page":1}"""
    }

    // ─── Mock data ──────────────────────────────────────────────────────────

    // Each triple: (title, category-tag, difficulty)
    data class MockRecipe(
        val title:      String,
        val category:   String,
        val difficulty: String,
        val imageUrl:   String,
        val cookTime:   Int,
        val servings:   Int,
        val calories:   Int,
    )

    private val MOCK_RECIPES = listOf(
        MockRecipe("Creamy Tuscan Chicken",         "chicken",    "Easy",   "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=800",   30,  4, 520),
        MockRecipe("Avocado Toast with Poached Egg","breakfast",  "Easy",   "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=800",   15,  2, 310),
        MockRecipe("Thai Basil Beef Stir Fry",      "beef",       "Medium", "https://images.unsplash.com/photo-1603360946369-dc9bb6258143?w=800",   25,  4, 480),
        MockRecipe("Classic Margherita Pizza",      "pizza",      "Medium", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=800",   45,  4, 560),
        MockRecipe("Chocolate Lava Cake",           "dessert",    "Medium", "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=800",   20,  2, 420),
        MockRecipe("Mediterranean Quinoa Bowl",     "vegetarian", "Easy",   "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800",      35,  4, 390),
        MockRecipe("Miso Ramen Bowl",               "soup",       "Hard",   "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800",   60,  2, 610),
        MockRecipe("Spicy Tuna Rolls",              "sushi",      "Hard",   "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800",   30,  4, 280),
        MockRecipe("Greek Lemon Chicken",           "chicken",    "Easy",   "https://images.unsplash.com/photo-1516684732162-798a0062be99?w=800",   45,  4, 490),
        MockRecipe("Homemade Fettuccine Alfredo",   "pasta",      "Medium", "https://images.unsplash.com/photo-1645112411341-6c4fd023714a?w=800",   40,  4, 680),
        MockRecipe("Authentic Carbonara",           "pasta",      "Medium", "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=800",   25,  2, 620),
        MockRecipe("Smoked Brisket",                "beef",       "Hard",   "https://images.unsplash.com/photo-1544025162-d76694265947?w=800",     480,  8, 710),
        MockRecipe("French Macarons",               "dessert",    "Hard",   "https://images.unsplash.com/photo-1558312657-b2dead03d494?w=800",      90, 24, 120),
        MockRecipe("Classic Tiramisu",              "dessert",    "Medium", "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=800",   30,  8, 380),
        MockRecipe("Hummus Trio Platter",           "vegetarian", "Easy",   "https://images.unsplash.com/photo-1571115177098-24ec42ed204d?w=800",   20,  6, 210),
    )

    private fun buildRecipeJson(recipe: MockRecipe): String {
        val id    = recipe.title.hashCode().let { if (it < 0) -it else it } % 10_000 + 1
        val likes = (id * 37) % 900 + 50
        val score = (id % 50 + 50).toDouble() / 10

        val ingredients = """[{"original":"2 cloves garlic, minced","name":"garlic"},{"original":"1 tbsp olive oil","name":"olive oil"},{"original":"Salt and pepper to taste","name":"salt"},{"original":"Fresh herbs for garnish","name":"herbs"}]"""
        val instructions = """[{"steps":[{"number":1,"step":"Prepare all ingredients and mise en place."},{"number":2,"step":"Heat oil in a large pan over medium-high heat."},{"number":3,"step":"Cook the main protein until golden brown."},{"number":4,"step":"Add aromatics and cook for 2 minutes."},{"number":5,"step":"Combine all components and season to taste."},{"number":6,"step":"Serve hot, garnished with fresh herbs."}]}]"""

        return """{"id":$id,"title":"${recipe.title}","image":"${recipe.imageUrl}","readyInMinutes":${recipe.cookTime},"servings":${recipe.servings},"aggregateLikes":$likes,"difficulty":"${recipe.difficulty}","dishTypes":["${recipe.category}"],"extendedIngredients":$ingredients,"analyzedInstructions":$instructions,"spoonacularScore":$score,"authorName":"Chef Demo","authorAvatar":"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200","calories":${recipe.calories}}"""
    }

    private val RECIPE_LIST_JSON: String by lazy {
        val items = MOCK_RECIPES.joinToString(",") { buildRecipeJson(it) }
        """{"results":[$items],"total":${MOCK_RECIPES.size},"page":1}"""
    }

    private val RECIPE_DETAIL_JSON: String by lazy {
        buildRecipeJson(MOCK_RECIPES.first())
    }
}
