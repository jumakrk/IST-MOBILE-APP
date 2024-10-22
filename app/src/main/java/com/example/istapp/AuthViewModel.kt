package com.example.istapp

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.istapp.utilities.PreferencesManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

// This AuthViewModel contains the logic for the authentication process
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // LiveData to hold the user's role
    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> = _userRole

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            getUserRoleFromFirestore(currentUser.uid)
        }
    }

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


    // Login functionality
    fun login(email: String, password: String, context: Context) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    // Checks the user's role during login
                    if (currentUser != null) {
                        getUserRoleFromFirestore(currentUser.uid)
                    }
                    if (currentUser != null && currentUser.isEmailVerified) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error("Your email is not verified. Please check your inbox and verify your email to continue.")
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login failed. Please try again.")
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Login failed. Please try again.")
                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
    }

    // Signup functionality with Username
    fun signup(email: String, password: String, firstname: String, lastname: String, context: Context) {
        if (email.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
            _authState.value = AuthState.Error("Email, password, and username cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {

                        //Specified admin emails
                        val adminEmails = listOf("jumakrk@gmail.com")
                        // Determine the user's role based on the email
                        val role = if (adminEmails.contains(email)) "admin" else "user"

                        // Update user profile with username
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstname $lastname") //converted concatenation to template
                            .build()
                        // Sign-in successful, save user details
                        saveUserDetailsToFirestore( "$firstname $lastname", role)

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileUpdateTask ->
                                if (profileUpdateTask.isSuccessful) {
                                    // Send email verification
                                    user.sendEmailVerification().addOnCompleteListener { verificationTask ->
                                        if (verificationTask.isSuccessful) {
                                            Toast.makeText(context, "A verification email has been sent. Please check your inbox.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    _authState.value = AuthState.Error("Failed to update profile: ${profileUpdateTask.exception?.message}")
                                }
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

    // Logout functionality
    fun logout(context: Context) {
        auth.signOut()

        // Reset the flag in PreferencesManager
        val preferencesManager = PreferencesManager(context)
        preferencesManager.setLoginMessageShown(false) // Reset the flag

        _authState.value = AuthState.UnAuthenticated

        // Show a toast message indicating successful logout
        Toast.makeText(context, "Successfully logged out", Toast.LENGTH_SHORT).show()
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
    fun resendVerificationEmail(context: Context) {
        val currentUser = auth.currentUser
        if (currentUser != null && !currentUser.isEmailVerified) {
            _authState.value = AuthState.Loading

            currentUser.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "A new verification email has been sent. Please check your inbox.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Failed to resend verification email. Please try again.")
                }
        } else {
            _authState.value = AuthState.Error("No authenticated user found or email is already verified.")
        }
    }

    // Check email verification status
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

    private fun saveUserDetailsToFirestore(username: String, role: String ) {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        user?.let {
            // Create a user map to store user details
            val firstname = it.displayName?.substringBefore(" ")
            val lastname = it.displayName?.substringAfter(" ")
            val userDetails = hashMapOf(
                "uid" to it.uid,
                "$firstname $lastname" to username,
                "email" to it.email,
                "role" to role
            )

            // Save user details in the "users" collection with the document ID as the user UID
            db.collection("users").document(it.uid)
                .set(userDetails)
                .addOnSuccessListener {
                    // Success handling
                    println("User details saved successfully")
                }
                .addOnFailureListener { e ->
                    // Failure handling
                    println("Error saving user details: ${e.message}")
                }
        }
    }

    // Retrieve user role from Firestore
    private fun getUserRoleFromFirestore(uid: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role") ?: "user"
                    _userRole.value = role
                } else {
                    _userRole.value = "user" // Default to "user" if no role is found
                }
            }
            .addOnFailureListener {
                _userRole.value = "user" // Default to "user" in case of an error
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
