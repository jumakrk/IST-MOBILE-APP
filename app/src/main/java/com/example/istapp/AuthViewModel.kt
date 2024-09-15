package com.example.istapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

// This AuthViewModel contains the logic for the authentication process
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>(AuthState.Loading)
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    // Check if the user is authenticated or not on app launch
    private fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.UnAuthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null && currentUser.isEmailVerified) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error("Your email is not verified. Please check your inbox and verify your email to continue.")
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed. Please try again.")
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Login failed. Please try again.")
            }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            _authState.value = AuthState.Success("A verification email has been sent. Please verify your email to continue.")
                        } else {
                            _authState.value = AuthState.Error("Failed to send verification email: ${verificationTask.exception?.message}")
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Sign up failed. Please try again.")
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Sign up failed. Please try again.")
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.UnAuthenticated
    }

    fun resetPassword(email: String) {
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("Email cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("A password reset email has been sent to $email. Please check your inbox.")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Failed to send password reset email. Please try again.")
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Failed to send password reset email. Please try again.")
            }
    }

    // Resend verification email functionality
    fun resendVerificationEmail() {
        val currentUser = auth.currentUser
        if (currentUser != null && !currentUser.isEmailVerified) {
            _authState.value = AuthState.Loading

            currentUser.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.Success("A new verification email has been sent. Please check your inbox.")
                    } else {
                        _authState.value = AuthState.Error("Failed to resend verification email: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Failed to resend verification email. Please try again.")
                }
        } else {
            _authState.value = AuthState.Error("No authenticated user found or email is already verified.")
        }
    }

    // AuthViewModel.kt

    fun checkEmailVerificationStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (currentUser.isEmailVerified) {
                        _authState.value = AuthState.Authenticated // Update the state to authenticated once the email is verified
                    } else {
                        _authState.value = AuthState.UnAuthenticated // Still unverified
                    }
                } else {
                    _authState.value = AuthState.Error("Failed to check email verification status.")
                }
            }
        } else {
            _authState.value = AuthState.UnAuthenticated
        }
    }
}

// Sealed class for representing authentication states
sealed class AuthState {
    data object Authenticated : AuthState()
    data object UnAuthenticated : AuthState()
    data object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
