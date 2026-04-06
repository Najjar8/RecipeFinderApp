package com.recipefinder.app.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

// ─── Flow helpers ────────────────────────────────────────────────────────────

/**
 * Wraps a [Flow]<T> so downstream collectors receive [Resource] wrappers
 * including a leading [Resource.Loading] emission and automatic error
 * conversion to [Resource.Error].
 */
fun <T> Flow<T>.asResource(): Flow<Resource<T>> =
    this
        .map<T, Resource<T>> { Resource.Success(it) }
        .onStart { emit(Resource.Loading()) }
        .catch  { e -> emit(Resource.Error(e.toUserMessage())) }

// ─── Throwable helpers ───────────────────────────────────────────────────────

fun Throwable.toUserMessage(): String =
    when (this) {
        is java.net.UnknownHostException -> "No internet connection"
        is java.net.SocketTimeoutException -> "Request timed out"
        is retrofit2.HttpException ->
            "Server error (${code()}). Please try again."
        else -> message ?: "An unexpected error occurred"
    }

// ─── String helpers ──────────────────────────────────────────────────────────

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.titlecase() }
    }

// ─── Int helpers ─────────────────────────────────────────────────────────────

/** Format minutes into a friendly "1h 30m" or "45 min" label. */
fun Int.formatCookTime(): String {
    if (this < 60) return "$this min"
    val hours   = this / 60
    val minutes = this % 60
    return if (minutes == 0) "${hours}h" else "${hours}h ${minutes}m"
}
