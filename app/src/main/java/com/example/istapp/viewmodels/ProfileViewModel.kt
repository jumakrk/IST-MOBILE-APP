package com.example.istapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserProfile(
    val username: String = "",
    val email: String = ""
)

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private var hasLoadedProfile = false

    fun fetchUserProfile() {
        if (!hasLoadedProfile) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                viewModelScope.launch {
                    _userProfile.value = _userProfile.value.copy(
                        email = currentUser.email ?: ""
                    )
                    
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(currentUser.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            val username = document.getString("username") ?: ""
                            _userProfile.value = _userProfile.value.copy(
                                username = username
                            )
                            hasLoadedProfile = true
                        }
                }
            }
        }
    }
} 