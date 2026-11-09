package com.example.uthtodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserUiState(
    val name: String = "Khách",
    val email: String = "Chưa đăng nhập tài khoản",
    val photoUrl: String = "",
    val isGoogleLinked: Boolean = false
)

class UserViewModel : ViewModel() {
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        val user = auth.currentUser
        if (user != null) {
            _uiState.update {
                it.copy(
                    name = user.displayName ?: "Khách",
                    email = user.email ?: "Chưa đăng nhập tài khoản",
                    photoUrl = user.photoUrl?.toString() ?: "",
                    isGoogleLinked = !user.isAnonymous
                )
            }
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            auth.signOut()
            _uiState.value = UserUiState()
            onSuccess()
        }
    }
}
