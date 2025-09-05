package com.example.guardia.ui.app.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardia.Data.Repository.LoginRepository
import com.example.guardia.model.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: LoginRepository = LoginRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginResult?>(null)
    val uiState: StateFlow<LoginResult?> = _uiState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginResult(errorMessage = "El correo y la contraseña no pueden estar vacíos")
            return
        }

        viewModelScope.launch {
            _loading.value = true
            val result = repository.login(email, password)
            _uiState.value = result
            _loading.value = false
        }
    }
}
