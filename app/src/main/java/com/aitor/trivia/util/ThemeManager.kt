package com.aitor.trivia.util

import android.content.Context
import com.aitor.trivia.R

object ThemeManager {

    private const val PREFS_NAME = "trivia_prefs"
    private const val KEY_THEME  = "theme_mode"

    const val DARK  = "dark"
    const val LIGHT = "light"

    fun saveTheme(context: Context, mode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_THEME, mode).apply()
    }

    fun getSavedTheme(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, DARK) ?: DARK
    }

    fun apply(context: Context) {
        val themeRes = if (getSavedTheme(context) == LIGHT)
            R.style.Theme_TriviaGame_Light
        else
            R.style.Theme_TriviaGame
        context.setTheme(themeRes)
    }

    fun applyAndSave(context: Context, mode: String) {
        saveTheme(context, mode)
    }
}