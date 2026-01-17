package com.example.chillbeads.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// State untuk merepresentasikan status proses otentikasi
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // State untuk status UI (loading, error, dll)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()
    
    // State untuk status login (sudah login atau belum)
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        _isLoggedIn.value = prefs.getBoolean("is_logged_in", false)
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1000) // Memberi jeda seolah-olah sedang loading dari server

            // --- Logika Validasi --- 
            if (email == "admin@chillbeads.com" && pass == "123456") {
                prefs.edit().putBoolean("is_logged_in", true).apply()
                _isLoggedIn.value = true
                _authState.value = AuthState.Idle // Kembali ke idle setelah sukses
            } else {
                _authState.value = AuthState.Error("Email atau password salah!")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.edit().clear().apply()
            _isLoggedIn.value = false
        }
    }
    
    // Fungsi untuk mereset state error setelah ditampilkan
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
