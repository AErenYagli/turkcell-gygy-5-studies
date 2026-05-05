package com.turkcell.libraryapp.data.repository

import com.turkcell.libraryapp.data.model.Profile
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlin.random.Random

class AuthRepository
{
    suspend fun signIn(email: String, password:String) : Result<Unit> = runCatching {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        studentNo: String?
    ) : Result<Unit> = runCatching {
        supabase.auth.signUpWith(Email){
            this.email = email
            this.password = password
        }

        val userId = supabase.auth.currentUserOrNull()?.id ?: error("Kullanıcı bulunamadı")

        supabase.postgrest["profiles"].insert(
            Profile(userId, "student", fullName, studentNo)
        )
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }
    fun getCurrentUserId() : String?
    {
        return supabase.auth.currentUserOrNull()?.id;
    }

    suspend fun getProfile(userId: String): Profile? = runCatching {
        supabase.postgrest["profiles"]
            .select { filter { eq("user_id", userId) }  }
            .decodeSingle<Profile>()
    }.getOrNull()
}