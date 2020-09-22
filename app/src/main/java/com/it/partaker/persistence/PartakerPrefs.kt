package com.it.partaker.persistence

import android.content.Context
import android.content.SharedPreferences

class PartakerPrefs(context: Context) {
    private val sharedPref: SharedPreferences

    init {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveRegisterAsUser(userResult: String?) {
        sharedPref.edit().putString(SAVED_USERS, userResult).apply()
    }

    fun getRegisterAsUser(): String? {
        val userString = sharedPref.getString(SAVED_USERS, "")
        return userString
    }

    fun clearEmployeePref(userKey: String?) {
        val employee = sharedPref.getString(userKey, "")
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        const val PREFS_NAME = "partaker_prefs"
        const val SAVED_USERS = "saved_users"
    }
}
