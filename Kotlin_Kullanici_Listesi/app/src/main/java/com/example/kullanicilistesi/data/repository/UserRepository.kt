package com.example.kullanicilistesi.data.repository

import com.example.kullanicilistesi.data.model.User
import com.example.kullanicilistesi.data.remote.RetrofitInstance

class UserRepository {
    suspend fun getUsers(): List<User> {
        return RetrofitInstance.api.getUsers()
    }
}