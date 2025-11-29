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
        const val ACTION_CAPTURE_SCREEN_TEXT = "com.cactus.peyokeys.CAPTURE_SCREEN_TEXT"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "ScreenReaderService connected")

        // Configure service to receive window content changes
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_FOCUSED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
        }
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Capture screen text on relevant events
        if (event != null) {
            captureScreenText()
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "ScreenReaderService interrupted")
    }

    /**
     * Capture all visible text from the screen
     */
    private fun captureScreenText() {
        try {
            val rootNode = rootInActiveWindow ?: return
            val screenText = extractTextFromNode(rootNode)

            if (screenText.isNotBlank()) {
                ScreenTextManager.updateScreenText(screenText)
            }

            rootNode.recycle()
        } catch (e: Exception) {
            Log.e(TAG, "Error capturing screen text", e)
        }
    }

    /**
     * Recursively extract text from accessibility node tree
     */
    private fun extractTextFromNode(node: AccessibilityNodeInfo): String {
        val textBuilder = StringBuilder()

        // Get text from current node
        if (node.text != null && node.text.isNotBlank()) {
            textBuilder.append(node.text.toString().trim())
            textBuilder.append(" ")
        }

        // Get content description if available
        if (node.contentDescription != null && node.contentDescription.isNotBlank()) {
            textBuilder.append(node.contentDescription.toString().trim())
            textBuilder.append(" ")
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

    /**
     * Force capture screen text (can be triggered externally)
     */
    fun forceCaptureScreenText(): String? {
        captureScreenText()
        return ScreenTextManager.getScreenText()
    }
}
