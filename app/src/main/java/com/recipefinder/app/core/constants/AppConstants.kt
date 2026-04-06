package com.recipefinder.app.core.constants

object AppConstants {

    // ─── Database ───────────────────────────────────────────────────────────
    const val DATABASE_NAME    = "recipe_finder_db"
    const val DATABASE_VERSION = 1

    // ─── Network ────────────────────────────────────────────────────────────
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS    = 30L
    const val WRITE_TIMEOUT_SECONDS   = 30L

    // ─── Pagination ─────────────────────────────────────────────────────────
    const val PAGE_SIZE       = 20
    const val INITIAL_PAGE    = 1
    const val PREFETCH_DISTANCE = 5

    // ─── Cache ──────────────────────────────────────────────────────────────
    const val CACHE_SIZE_MB = 10L
    const val CACHE_MAX_AGE_SECONDS    = 60 * 5       // 5 min
    const val CACHE_MAX_STALE_SECONDS  = 60 * 60 * 24 // 24 h offline

    // ─── UI ─────────────────────────────────────────────────────────────────
    const val DEBOUNCE_MILLIS        = 400L
    const val ANIMATION_DURATION_MS  = 300
    const val RECIPE_CARD_IMAGE_HEIGHT_DP = 200
}
