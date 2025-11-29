package com.cactus.peyokeys

import android.util.Log

/**
 * Memory Reader Service - simulates a memory store for user context
 * Acts as a RAG (Retrieval-Augmented Generation) pipeline placeholder
 * Currently uses hardcoded memories; can be extended to query real APIs later
 */
object MemoryReaderService {
    private const val TAG = "MemoryReaderService"

    // Hardcoded memory entries (simulated memory store)
    private val MEMORIES = listOf(
        "December 2nd is a free day"
    )

    /**
     * Retrieve memory context (currently simulated with hardcoded values)
     * @return Concatenated memory text, or null if no memories available
     */
    fun getMemoryContext(): String? {
        return try {
            if (MEMORIES.isEmpty()) {
                Log.d(TAG, "No memories available")
                null
            } else {
                val memoryText = MEMORIES.joinToString("\n")
                Log.d(TAG, "Retrieved ${MEMORIES.size} memory entries, ${memoryText.length} characters")
                memoryText
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving memory context", e)
            null
        }
    }
}
