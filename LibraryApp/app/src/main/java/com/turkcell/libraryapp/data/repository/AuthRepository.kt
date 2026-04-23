package com.turkcell.libraryapp.data.repository

import kotlinx.coroutines.delay
import kotlin.random.Random

class AuthRepository
{
    suspend fun signIn(email: String, password:String) : Result<Unit> = runCatching {
        delay(2000) // dışarıya istek atıyomuş gibi

        val isSuccess = Random.nextBoolean() // %50 %50
        if(isSuccess)
            Unit
        else
            throw Exception("Fake login failed")
    }

    suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        delay(2000)
        val isSuccess = Random.nextBoolean()
        if (isSuccess)
            Unit
        else
            throw Exception("Kayıt başarısız")
    }
}
