package com.turkcell.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.libraryapp.data.model.Profile
import com.turkcell.libraryapp.data.repository.AuthRepository
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Sistem bu 4ünden birinde olabilir.
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class SessionState {
    object Initializing : SessionState()
    object Unauthenticated : SessionState()
    data class Authenticated(val role: String) : SessionState()
}

class AuthViewModel : ViewModel()
{
    private val repository = AuthRepository()

    // Auth sayfalarını yönetmek..
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState;

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Initializing);
    val sessionState: StateFlow<SessionState> = _sessionState;

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile;


    init {
        viewModelScope.launch {
            // session state hesaplama..
            supabase.auth.sessionStatus.collect { status ->
                when (status)
                {
                    is SessionStatus.Authenticated -> {
                        val userId = repository.getCurrentUserId()
                        if(userId==null)
                        {
                            _profile.value = null
                            _sessionState.value = SessionState.Unauthenticated
                            return@collect
                        }
                        val profile = repository.getProfile(userId)
                        _profile.value = profile
                        _sessionState.value = SessionState.Authenticated(profile?.role ?: "student")
                    }
                    SessionStatus.Initializing -> {
                        _sessionState.value = SessionState.Initializing
                    }
                    else -> {
                        _profile.value = null
                        _sessionState.value = SessionState.Unauthenticated
                    }
                }
            }
        }
    }

    fun signIn(email: String, password: String)
    {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository
                .signIn(email, password)
                .onSuccess {
                    val userId = repository.getCurrentUserId(); // userId Geliyo mu?
                    if(userId != null)
                    {
                        val profile = repository.getProfile(userId); // profile geliyo mu?
                        _profile.value = profile; // doğru set ediliyor mu?
                        _authState.value = AuthState.Success("student")
                    }else{
                        _authState.value = AuthState.Error("Profil bulunamadı.")
                    }
                }
                .onFailure { ex -> _authState.value = AuthState.Error(ex.message ?: "Giriş başarısız") }
        }
    }

    fun signUp(
        email: String,
        password: String,
        fullName: String,
        studentNo: String?
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository
                .signUp(email, password, fullName, studentNo)
                .onSuccess { result -> _authState.value = AuthState.Success("student") }
                .onFailure { ex -> _authState.value = AuthState.Error(ex.message ?: "Kayıt başarısız") }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle;
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _profile.value = null
            _authState.value = AuthState.Idle
        }
    }
}