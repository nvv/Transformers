package com.vnamashko.transformers.core

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

/**
 * @author Vlad Namashko
 */
class Storage (context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCE, Activity.MODE_PRIVATE)

    var token: String?
        get() = preferences.getString(AUTH_TOKEN, null)
        set(value) = preferences.edit().putString(AUTH_TOKEN, value).apply()

    val isNewUser: Boolean
        get() = token == null


    companion object {
        private const val PREFERENCE = "PREFERENCE"
        private const val AUTH_TOKEN = "AUTH_TOKEN"
    }
}