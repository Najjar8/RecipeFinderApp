package com.recipefinder.app.core.util

/**
 * A discriminated union that wraps a repository result so the presentation
 * layer never needs to deal with raw exceptions or null checks.
 *
 * Usage:
 *   when (result) {
 *       is Resource.Loading -> showSpinner()
 *       is Resource.Success -> render(result.data)
 *       is Resource.Error   -> showError(result.message)
 *   }
 */
sealed class Resource<out T> {

    /** Initial / in-flight state. [data] may hold a stale cached value. */
    data class Loading<T>(val data: T? = null) : Resource<T>()

    /** Terminal success state carrying the result payload. */
    data class Success<T>(val data: T) : Resource<T>()

    /** Terminal error state. Always carries a human-readable [message]. */
    data class Error<T>(
        val message: String,
        val data: T? = null          // stale data can still be shown
    ) : Resource<T>()
}
