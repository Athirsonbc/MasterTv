package com.mastertv.app.auth

import android.content.Context

class AuthManager(private val context: Context) {

    private val sp = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveCredentials(username: String, password: String) {
        sp.edit()
            .putString("user", username)
            .putString("pass", password)
            .apply()
    }

    fun getUsername(): String? = sp.getString("user", null)
    fun getPassword(): String? = sp.getString("pass", null)

    fun logout() {
        sp.edit().clear().apply()
    }
}
