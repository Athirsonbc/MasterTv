package com.mastertv.app.auth

import android.content.Context

class AuthManager(private val context: Context) {

    private val sp = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveCredentials(username: String, password: String) {
        sp.edit()
            .putString("user", username)
            .putString("pass", password)
            .apply()
        // marcar o tempo caso seja um teste (considere que generate test irá setar timestamp)
    }

    fun getUsername(): String? = sp.getString("user", null)
    fun getPassword(): String? = sp.getString("pass", null)

    fun logout() {
        sp.edit().remove("user").remove("pass").apply()
    }

    fun markTestGenerated(timestampMillis: Long) {
        sp.edit().putLong("test_generated_at", timestampMillis).apply()
    }

    fun getTestGeneratedAt(): Long = sp.getLong("test_generated_at", 0L)
}
