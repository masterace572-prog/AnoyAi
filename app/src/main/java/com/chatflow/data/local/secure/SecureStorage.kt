package com.chatflow.data.local.secure

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorage @Inject constructor(
    private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_api_keys",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveKey(keyId: String, apiKeyValue: String) {
        sharedPreferences.edit().putString(keyId, apiKeyValue).apply()
    }

    fun getKey(keyId: String): String? {
        return sharedPreferences.getString(keyId, null)
    }

    fun removeKey(keyId: String) {
        sharedPreferences.edit().remove(keyId).apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
