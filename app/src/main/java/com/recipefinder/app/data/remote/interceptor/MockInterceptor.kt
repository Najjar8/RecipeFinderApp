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
        // Filter mock recipes by title match
        val filtered = MOCK_RECIPES.filter {
            it.first.lowercase().contains(query) ||
            it.second.lowercase().contains(query)
        }
        val items = filtered.joinToString(",") { buildRecipeJson(it) }
        return """{"results":[$items],"total":${filtered.size},"page":1}"""
    }

    // ─── Mock data ──────────────────────────────────────────────────────────

    // Each triple: (title, category-tag, difficulty)
    private val MOCK_RECIPES = listOf(
        Triple("Creamy Tuscan Chicken",        "chicken",  "Easy"),
        Triple("Avocado Toast with Poached Egg","breakfast","Easy"),
        Triple("Thai Basil Beef Stir Fry",     "beef",     "Medium"),
        Triple("Classic Margherita Pizza",     "pizza",    "Medium"),
        Triple("Chocolate Lava Cake",          "dessert",  "Medium"),
        Triple("Mediterranean Quinoa Bowl",    "vegetarian","Easy"),
        Triple("Miso Ramen Bowl",              "soup",     "Hard"),
        Triple("Spicy Tuna Rolls",             "sushi",    "Hard"),
        Triple("Greek Lemon Chicken",          "chicken",  "Easy"),
        Triple("Homemade Fettuccine Alfredo",  "pasta",    "Medium"),
        Triple("Authentic Carbonara",          "pasta",    "Medium"),
        Triple("BBQ Pulled Pork",              "pork",     "Easy"),
        Triple("Smoked Brisket",               "beef",     "Hard"),
        Triple("French Macarons",              "dessert",  "Hard"),
        Triple("Classic Tiramisu",             "dessert",  "Medium"),
        Triple("Hummus Trio Platter",          "vegetarian","Easy"),
    )

    private val IMAGES = listOf(
        "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=800",
        "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=800",
        "https://images.unsplash.com/photo-1603360946369-dc9bb6258143?w=800",
        "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=800",
        "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=800",
        "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800",
        "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800",
        "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=800",
    )

    private fun buildRecipeJson(recipe: Triple<String, String, String>): String {
        val (title, category, difficulty) = recipe
        val id       = title.hashCode().let { if (it < 0) -it else it } % 10_000 + 1
        val image    = IMAGES[id % IMAGES.size]
        val cookTime = listOf(15, 20, 25, 30, 35, 45, 60, 90)[id % 8]
        val servings = listOf(2, 4, 4, 6)[id % 4]
        val likes    = (id * 37) % 900 + 50

        val ingredients = """[
            {"original":"2 cloves garlic, minced","name":"garlic"},
            {"original":"1 tbsp olive oil","name":"olive oil"},
            {"original":"Salt and pepper to taste","name":"salt"},
            {"original":"Fresh herbs for garnish","name":"herbs"}
        ]"""

        val instructions = """[{"steps":[
            {"number":1,"step":"Prepare all ingredients and mise en place."},
            {"number":2,"step":"Heat oil in a large pan over medium-high heat."},
            {"number":3,"step":"Cook the main protein until golden brown."},
            {"number":4,"step":"Add aromatics and cook for 2 minutes."},
            {"number":5,"step":"Combine all components and season to taste."},
            {"number":6,"step":"Serve hot, garnished with fresh herbs."}
        ]}]"""

        return """
        {
          "id":$id,
          "title":"$title",
          "image":"$image",
          "readyInMinutes":$cookTime,
          "servings":$servings,
          "aggregateLikes":$likes,
          "difficulty":"$difficulty",
          "dishTypes":["$category"],
          "extendedIngredients":$ingredients,
          "analyzedInstructions":$instructions,
          "spoonacularScore":${(id % 50 + 50).toDouble() / 10},
          "authorName":"Chef Demo",
          "authorAvatar":"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
          "calories":${(id % 300 + 200)}
        }
        """.trimIndent()
    }

    private val RECIPE_LIST_JSON: String by lazy {
        val items = MOCK_RECIPES.joinToString(",") { buildRecipeJson(it) }
        """{"results":[$items],"total":${MOCK_RECIPES.size},"page":1}"""
    }

    private val RECIPE_DETAIL_JSON: String by lazy {
        buildRecipeJson(MOCK_RECIPES.first())
    }
}
