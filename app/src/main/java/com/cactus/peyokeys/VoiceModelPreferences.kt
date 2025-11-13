package com.cactus.peyokeys

import android.content.Context
import android.content.SharedPreferences

object VoiceModelPreferences {
    private const val PREFS_NAME = "voice_model_prefs"
    private const val KEY_ACTIVE_MODEL = "active_model_slug"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setActiveModel(context: Context, modelSlug: String) {
        getPrefs(context).edit().putString(KEY_ACTIVE_MODEL, modelSlug).apply()
    }

    fun getActiveModel(context: Context): String? {
        return getPrefs(context).getString(KEY_ACTIVE_MODEL, null)
    }
}
