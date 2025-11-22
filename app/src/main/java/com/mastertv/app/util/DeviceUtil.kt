package com.mastertv.app.util

import android.content.Context
import android.provider.Settings
import java.security.MessageDigest
import java.util.UUID

object DeviceUtil {

    private const val PREF = "mastertv_device"
    private const val KEY_FALLBACK = "device_fallback"

    // retorna um código curto (hash hex) do device id
    fun getDeviceCode(context: Context): String {
        val raw = getStableId(context)
        return sha1Hex(raw).substring(0, 12).uppercase()
    }

    // tenta ANDROID_ID, senão gera fallback e salva
    private fun getStableId(context: Context): String {
        val id = try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            null
        }
        if (!id.isNullOrBlank()) return id
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        var fb = sp.getString(KEY_FALLBACK, null)
        if (fb == null) {
            fb = UUID.randomUUID().toString()
            sp.edit().putString(KEY_FALLBACK, fb).apply()
        }
        return fb
    }

    private fun sha1Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
