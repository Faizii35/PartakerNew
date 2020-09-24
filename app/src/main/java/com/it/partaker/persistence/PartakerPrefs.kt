package com.it.partaker.persistence

import android.content.Context
import android.content.SharedPreferences

class PartakerPrefs(context: Context) {
    private val sharedPref: SharedPreferences

    init {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveNameUser(userResult: String?) {
        sharedPref.edit().putString(SAVED_USER_NAME, userResult).apply()
    }

    fun getNameUser(): String? {
        val userString = sharedPref.getString(SAVED_USER_NAME, "")
        return userString
    }

    fun savePhoneUser(userResult: String?) {
        sharedPref.edit().putString(SAVED_USER_PHONE, userResult).apply()
    }

    fun getPhoneUser(): String? {
        val userString = sharedPref.getString(SAVED_USER_PHONE, "")
        return userString
    }

    fun saveCityUser(userResult: String?) {
        sharedPref.edit().putString(SAVED_USER_CITY, userResult).apply()
    }

    fun getCityUser(): String? {
        val userString = sharedPref.getString(SAVED_USER_CITY, "")
        return userString
    }

    fun saveEmailUser(userResult: String?) {
        sharedPref.edit().putString(SAVED_USER_EMAIL, userResult).apply()
    }

    fun getEmailUser(): String? {
        val userString = sharedPref.getString(SAVED_USER_EMAIL, "")
        return userString
    }

    fun saveProfileUser(userResult: String?) {
        sharedPref.edit().putString(SAVED_USER_PROFILE, userResult).apply()
    }

    fun getProfileUser(): String? {
        val userString = sharedPref.getString(SAVED_USER_PROFILE, "")
        return userString
    }

    fun saveBloodUser(userResult: String?) {
        sharedPref.edit().putString(SAVED_USER_BLOOD, userResult).apply()
    }

    fun getBloodUser(): String? {
        val userString = sharedPref.getString(SAVED_USER_BLOOD, "")
        return userString
    }


    fun saveGenderUser(userResult: String?) {
        sharedPref.edit().putString(SAVED_USER_GENDER, userResult).apply()
    }

    fun getGenderUser(): String? {
        val userString = sharedPref.getString(SAVED_USER_GENDER, "")
        return userString
    }

    fun saveRegisterAsUser(userResult: String?) {
        sharedPref.edit().putString(SAVED_USER_REGISTER_AS, userResult).apply()
    }

    fun getRegisterAsUser(): String? {
        val userString = sharedPref.getString(SAVED_USER_REGISTER_AS, "")
        return userString
    }

    fun clearUserPref() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        const val PREFS_NAME = "partaker_prefs"
        const val SAVED_USER_NAME = "saved_user_name"
        const val SAVED_USER_PHONE = "saved_user_phone"
        const val SAVED_USER_CITY = "saved_user_city"
        const val SAVED_USER_EMAIL = "saved_user_email"
        const val SAVED_USER_PROFILE = "saved_user_profile"
        const val SAVED_USER_BLOOD = "saved_user_blood"
        const val SAVED_USER_GENDER = "saved_user_gender"
        const val SAVED_USER_REGISTER_AS = "saved_user_register_as"

    }
}
