package com.cactus.peyokeys

/**
 * System prompts for different app contexts
 */
object SystemPrompts {

    // App package identifiers
    private val EMAIL_APPS = setOf(
        "com.google.android.gm",           // Gmail
        "com.microsoft.office.outlook",    // Outlook
        "com.yahoo.mobile.client.android.mail", // Yahoo Mail
        "com.samsung.android.email.provider", // Samsung Email
        "com.android.email"                // Default Email
    )

    private val NOTES_APPS = setOf(
        "com.google.android.keep",         // Google Keep
        "com.samsung.android.app.notes",   // Samsung Notes
        "com.microsoft.office.onenote",    // OneNote
        "com.evernote",                    // Evernote
        "com.simplemobiletools.notes.pro", // Simple Notes
        "com.notion.id"                    // Notion
    )

    /**
     * Get the appropriate system prompt based on the app package name
     */
    fun getSystemPrompt(packageName: String?, screenContext: String?, memoryContext: String? = null): String {
        val appSpecificPrompt = when {
            packageName != null && EMAIL_APPS.contains(packageName) -> getEmailPrompt()
            packageName != null && NOTES_APPS.contains(packageName) -> getNotesPrompt()
            else -> getGeneralPrompt()
        }

        // Build complete prompt: base guidelines + app-specific + memory + screen context
        return buildString {
            append(getBasePrompt())
            append("\n\n")
            append(appSpecificPrompt)

            // Add memory context first (if available)
            if (memoryContext != null && memoryContext.isNotBlank()) {
                append("\n\nUser's Memory (important facts to remember):\n")
                append(memoryContext.take(2000)) // Limit to 2000 chars to avoid token overflow
            }

            // Add screen context second (if available)
            if (screenContext != null && screenContext.isNotBlank()) {
                append("\n\nContext (what the user is currently viewing):\n")
                append(screenContext.take(2000)) // Limit to 2000 chars to avoid token overflow
            }
        }
    }

    private fun getBasePrompt(): String {
        return """/no_think You are a helpful writing assistant.

CRITICAL FORMATTING RULES (MUST FOLLOW):
- Do not provide suggestions or extra guidance at the end
- Write only in plain text with correct capitalization
- Do not use Markdown formatting
- Do not use special characters or emojis unless they are explicitly requested
- Generate only the requested text, without any meta-commentary or explanations"""
    }

    private fun getEmailPrompt(): String {
        return """EMAIL WRITING TASK:
Your task is to help compose professional and clear email responses or drafts.

Guidelines:
- Generate email text that is professional, clear, and concise
- Match the tone of the conversation shown in the screen context
- If replying to an email, address the points mentioned in the original message which will be provided in the context
- Use appropriate greetings and closings based on the formality of the conversation
- Keep responses brief unless a detailed response is specifically requested
- Do not include subject lines unless explicitly asked
- Generate only the email body text
- End emails always with 
Regards,
Hackathon Tester
 - The senders name will always be available in the context"""
    }

    private fun getNotesPrompt(): String {
        return """NOTE-TAKING TASK:
Your task is to help create clear and organized notes.

Guidelines:
- Generate concise, well-structured notes based on the user's request
- Use bullet points or numbered lists when appropriate
- Keep the tone casual and direct
- Focus on clarity and easy readability
- Expand on ideas when asked, but stay focused
- If context is provided use that information to build on the generation
- Do not add unnecessary formatting or headers unless requested
- Generate only the note content"""
    }

    private fun getGeneralPrompt(): String {
        return """GENERAL WRITING TASK:
Generate the requested content concisely and clearly.

Guidelines:
- Follow the user's instructions exactly
- Match the tone and style to the context
- Be concise unless detail is specifically requested
- Use the context to understand what the user is working on"""
    }

    /**
     * Detect app type from package name for logging/debugging
     */
    fun getAppType(packageName: String?): String {
        return when {
            packageName == null -> "unknown"
            EMAIL_APPS.contains(packageName) -> "email"
            NOTES_APPS.contains(packageName) -> "notes"
            else -> "general"
        }
    }
}
