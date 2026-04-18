package com.example.kullanicilistesi.data.remote

import com.example.kullanicilistesi.data.model.User
import retrofit2.http.GET

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>
}