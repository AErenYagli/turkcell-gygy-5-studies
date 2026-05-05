package com.turkcell.libraryapp.data.internal

import android.content.Context
import android.content.SharedPreferences

// SharedPreferences -> Küçük anahtar-değer (key-value) şeklinde veri tutabildiğimiz (cihaza kaydedebildiğimiz) en eski ve en basit Android API'sidir.
// Veri uygulamaya özel bir XML dosyasında tutulur.

// data/com.turkcell/shared_prefs/dosya_adi.xml

// Neden MODERN projelerde tercih edilmez?

// apply() async -> commit() ana thread'i bloklar
// hata yönetimi yok
// tip güvenliği zayıf, sadece ilkel tipler (int,long,float,bool,string)

class ThemePref(context: Context)
{
    private val prefs: SharedPreferences = context.getSharedPreferences("ayarlar", Context.MODE_PRIVATE)

    suspend fun saveTheme(lightMode: Boolean)
    {
        prefs
            .edit()
            .putBoolean("light_mode", lightMode)
            .apply();
    }

    fun getTheme(): Boolean = prefs.getBoolean("light_mode", true)
}