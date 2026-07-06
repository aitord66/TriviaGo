package com.aitor.trivia.util

import android.content.Context

object UserSession {
    private const val PREFS_NAME = "trivia_prefs"
    private const val KEY_USER   = "username"
    private const val KEY_ID     = "id_user"

    fun save(context: Context, username: String, idUser: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_USER, username)
            .putInt(KEY_ID, idUser)
            .apply()
    }

    fun getUsername(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_USER, "") ?: ""

    fun getIdUser(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_ID, -1)

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .remove(KEY_USER).remove(KEY_ID).apply()
    }

    fun isLoggedIn(context: Context) = getIdUser(context) != -1
}
