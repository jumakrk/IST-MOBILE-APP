package com.example.istapp.utilities

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LOGIN_MESSAGE_SHOWN = "login_message_shown"
    }

    // Save the flag
    fun setLoginMessageShown(shown: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_LOGIN_MESSAGE_SHOWN, shown).apply()
    }

    // Retrieve the flag
    fun isLoginMessageShown(): Boolean {
        return sharedPreferences.getBoolean(KEY_LOGIN_MESSAGE_SHOWN, false)
    }

}