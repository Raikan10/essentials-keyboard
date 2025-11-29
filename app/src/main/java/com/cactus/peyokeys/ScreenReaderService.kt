package com.cactus.peyokeys

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Accessibility service to read screen text for AI context
 */
class ScreenReaderService : AccessibilityService() {

    companion object {
        private const val TAG = "ScreenReaderService"
        private var instance: ScreenReaderService? = null

        fun getInstance(): ScreenReaderService? = instance
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "ScreenReaderService connected")

        // Minimal configuration - we only need to be able to read the window when requested
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No need to do anything here - we'll capture on-demand
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onInterrupt() {
        Log.d(TAG, "ScreenReaderService interrupted")
    }

    /**
     * Capture all visible text from the screen (called on-demand)
     */
    fun captureScreenText(): String? {
        return try {
            val rootNode = rootInActiveWindow ?: run {
                Log.w(TAG, "No root node available")
                return null
            }
            val screenText = extractTextFromNode(rootNode)
            rootNode.recycle()

            if (screenText.isNotBlank()) {
                Log.d(TAG, "Captured ${screenText.length} characters of screen text")
                screenText
            } else {
                Log.d(TAG, "No text content found on screen")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error capturing screen text", e)
            null
        }
    }

    /**
     * Recursively extract text from accessibility node tree
     * Captures all visible text with minimal filtering
     */
    private fun extractTextFromNode(node: AccessibilityNodeInfo): String {
        val textBuilder = StringBuilder()

        // Extract text from this node
        val nodeText = node.text
        if (nodeText != null && nodeText.isNotEmpty()) {
            val text = nodeText.toString().trim()

            if (text.isNotEmpty()) {
                // Add newline for longer text blocks (likely paragraphs)
                if (text.length > 50) {
                    textBuilder.append(text)
                    textBuilder.append("\n")
                } else {
                    textBuilder.append(text)
                    textBuilder.append(" ")
                }
            }
        }

        // Also get content description if text is empty
        if (nodeText == null || nodeText.isEmpty()) {
            val contentDesc = node.contentDescription
            if (contentDesc != null && contentDesc.isNotEmpty()) {
                val desc = contentDesc.toString().trim()
                if (desc.isNotEmpty() && desc.length > 2) {
                    textBuilder.append(desc)
                    textBuilder.append(" ")
                }
            }
        }

        // Recursively get text from child nodes
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            if (childNode != null) {
                textBuilder.append(extractTextFromNode(childNode))
                childNode.recycle()
            }
        }

        return textBuilder.toString()
    }
}
