package com.turkcell.libraryapp.data.internal

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

// EncryptedSharedPreferences / EncryptedFile
// - Veri şifrelenir
// -> Şifreleme anahtarını Android Keystore'da tutar.

// Android Keystore
//

// androidx.security:security-crypto:1.1.0

class EncryptedStorage(context: Context) {
    private val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(); //Bu görünmez anahtarla istediğin veriyi şifrele..

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "guvenli_kasa",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token:String)
    {
        prefs.edit().putString("token",token).apply()
    }
}