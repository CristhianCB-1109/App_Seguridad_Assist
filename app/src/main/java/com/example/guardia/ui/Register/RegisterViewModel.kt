package com.example.guardia.ui.Register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardia.model.RegisterResult
import com.example.guardia.Data.Repository.RegisterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: RegisterRepository = RegisterRepository()
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterResult?>(null)
    val registerState: StateFlow<RegisterResult?> = _registerState

    fun register(email: String, password: String, rol: String = "alumno") {
        viewModelScope.launch {
            val result = repository.registerUser(email, password, rol)
            _registerState.value = result
        }
    }
}
