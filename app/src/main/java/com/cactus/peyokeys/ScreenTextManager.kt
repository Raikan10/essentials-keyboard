package com.cactus.peyokeys

import android.util.Log

/**
 * Singleton manager to share screen text between AccessibilityService and InputMethodService
 */
object ScreenTextManager {
    private const val TAG = "ScreenTextManager"
    private var cachedScreenText: String? = null
    private var lastUpdateTime: Long = 0

    /**
     * Update the cached screen text from accessibility service
     */
    fun updateScreenText(text: String) {
        cachedScreenText = text
        lastUpdateTime = System.currentTimeMillis()
        Log.d(TAG, "Screen text updated: ${text.take(100)}...")
    }

    /**
     * Get the cached screen text
     * @return The screen text or null if not available or too old
     */
    fun getScreenText(maxAgeMs: Long = 5000): String? {
        val age = System.currentTimeMillis() - lastUpdateTime
        return if (age <= maxAgeMs) {
            cachedScreenText
        } else {
            Log.d(TAG, "Cached screen text is too old ($age ms)")
            null
        }
    }

    /**
     * Clear the cached screen text
     */
    fun clear() {
        cachedScreenText = null
        lastUpdateTime = 0
    }
}
