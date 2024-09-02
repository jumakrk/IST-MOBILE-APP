package com.example.istapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

//This AuthViewModel Contains the logic for the authentication process

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>(AuthState.Loading)
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    //When you launch the app it will check if the user is authenticated or not

    private fun checkAuthStatus(){
        if (auth.currentUser == null){
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

        // Sign in with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null && currentUser.isEmailVerified) {
                        // When Email is verified, proceed with login
                        _authState.value = AuthState.Authenticated
                    } else {
                        // hen Email is not verified
                        _authState.value = AuthState.Success("Your email is not verified. Please check your inbox and verify your email to continue.")
                    }
                } else {
                    // Handle sign-in failure
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed. Please try again.")
                }
            }
            .addOnFailureListener { exception ->
                // Handle any other errors
                _authState.value = AuthState.Error(exception.message ?: "Login failed. Please try again.")
            }
    }



    fun signup(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        // Create a new user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Send a verification email to the user
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            _authState.value = AuthState.Success("A verification email has been sent. Please verify your email to continue.")
                        } else {
                            _authState.value = AuthState.Error("Failed to send verification email: ${verificationTask.exception?.message}")
                        }
                    }
                } else {
                    // Handle sign-up failure
                    _authState.value = AuthState.Error(task.exception?.message ?: "Sign up failed. Please try again.")
                }
            }
            .addOnFailureListener { exception ->
                // Handle any other errors
                _authState.value = AuthState.Error(exception.message ?: "Sign up failed. Please try again.")
            }
    }


    fun logout(){
        auth.signOut()
        _authState.value = AuthState.UnAuthenticated
    }

    fun resetPassword(email: String) {
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("Email cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        // Send password reset email
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    _authState.value = AuthState.Success("A password reset email has been sent to $email. Please check your inbox.")
                } else {
                    // Failed to send reset email
                    _authState.value = AuthState.Error(task.exception?.message ?: "Failed to send password reset email. Please try again.")
                }
            }
            .addOnFailureListener { exception ->
                // Handle any other errors
                _authState.value = AuthState.Error(exception.message ?: "Failed to send password reset email. Please try again.")
            }
    }

}



sealed class  AuthState {
    data object Authenticated : AuthState()
    data object UnAuthenticated : AuthState()
    data object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}