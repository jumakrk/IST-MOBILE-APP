package com.example.istapp

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.istapp.utilities.PreferencesManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.example.istapp.models.User

// This AuthViewModel contains the logic for the authentication process
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // LiveData to hold the user's role
    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> = _userRole

    // Add these properties to the AuthViewModel class
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

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
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        if (!currentUser.isEmailVerified) {
                            _authState.value = AuthState.Error("Please verify your email before logging in")
                            auth.signOut() // Sign out if email isn't verified
                            return@addOnCompleteListener
                        }
                        
                        // Get user role and set it immediately
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(currentUser.uid).get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val role = document.getString("role") ?: "user"
                                    _userRole.value = role
                                    _authState.value = AuthState.Authenticated
                                } else {
                                    _userRole.value = "user"
                                    _authState.value = AuthState.Error("User data not found")
                                }
                            }
                            .addOnFailureListener {
                                _authState.value = AuthState.Error("Failed to fetch user role: ${it.message}")
                            }
                    }
                } else {
                    when (task.exception?.message) {
                        "There is no user record corresponding to this identifier. The user may have been deleted." ->
                            _authState.value = AuthState.Error("No account found with this email")
                        "The password is invalid or the user does not have a password." ->
                            _authState.value = AuthState.Error("Incorrect password")
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                            _authState.value = AuthState.Error("Network error. Please check your internet connection")
                        else ->
                            _authState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                    }
                }
            }
    }

    // Signup functionality with Username
    fun signup(email: String, password: String, firstname: String, lastname: String) {
        if (email.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters long")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val adminEmails = listOf("jumakrk@gmail.com")
                        val role = if (adminEmails.contains(email)) "admin" else "user"
                        _userRole.value = role

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName("$firstname $lastname")
                            .build()

                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    saveUserDetailsToFirestore(firstname, lastname, email, role)
                                    
                                    it.sendEmailVerification()
                                        .addOnCompleteListener { emailTask ->
                                            if (emailTask.isSuccessful) {
                                                _authState.value = AuthState.Success("Account created! Please check your email for verification")
                                            } else {
                                                _authState.value = AuthState.Error("Account created but failed to send verification email: ${emailTask.exception?.message}")
                                            }
                                        }
                            } else {
                                _authState.value = AuthState.Error("Failed to update profile: ${profileTask.exception?.message}")
                            }
                        }
                    }
                } else {
                    when (task.exception?.message) {
                        "The email address is already in use by another account." ->
                            _authState.value = AuthState.Error("This email is already registered")
                        "The email address is badly formatted." ->
                            _authState.value = AuthState.Error("Please enter a valid email address")
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                            _authState.value = AuthState.Error("Network error. Please check your internet connection")
                        else ->
                            _authState.value = AuthState.Error(task.exception?.message ?: "Failed to create account")
                    }
                }
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
            _authState.value = AuthState.Error("Please enter your email address")
            return
        }

        _authState.value = AuthState.Loading

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Password reset email sent to $email")
                } else {
                    when (task.exception?.message) {
                        "There is no user record corresponding to this identifier. The user may have been deleted." ->
                            _authState.value = AuthState.Error("No account found with this email")
                        "The email address is badly formatted." ->
                            _authState.value = AuthState.Error("Please enter a valid email address")
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                            _authState.value = AuthState.Error("Network error. Please check your internet connection")
                        else ->
                            _authState.value = AuthState.Error(task.exception?.message ?: "Failed to send reset email")
                    }
                }
            }
    }

    // Resend verification email functionality
    fun resendVerificationEmail() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Error("No user is currently signed in")
            return
        }

        if (currentUser.isEmailVerified) {
            _authState.value = AuthState.Error("Email is already verified")
            return
        }

        _authState.value = AuthState.Loading

        currentUser.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success("Verification email sent. Please check your inbox")
                } else {
                    when (task.exception?.message) {
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                            _authState.value = AuthState.Error("Network error. Please check your internet connection")
                        else ->
                            _authState.value = AuthState.Error(task.exception?.message ?: "Failed to send verification email")
                    }
                }
            }
    }

    // Check email verification status
    fun checkEmailVerificationStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (currentUser.isEmailVerified) {
                        // Get user role and set it immediately after verification
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(currentUser.uid).get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val role = document.getString("role") ?: "user"
                                    _userRole.value = role
                                    _authState.value = AuthState.Authenticated
                                } else {
                                    _userRole.value = "user"
                                    _authState.value = AuthState.Authenticated
                                }
                            }
                            .addOnFailureListener {
                                _userRole.value = "user"
                                _authState.value = AuthState.Authenticated
                            }
                    } else {
                        _authState.value = AuthState.UnAuthenticated
                    }
                } else {
                    _authState.value = AuthState.Error("Failed to check email verification status")
                }
            }
        } else {
            _authState.value = AuthState.UnAuthenticated
        }
    }

    private fun saveUserDetailsToFirestore(firstname: String, lastname: String, email: String, role: String) {
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()

        user?.let {
            val userDetails = hashMapOf(
                "uid" to it.uid,
                "username" to "$firstname $lastname",  // Store full name as username
                "firstname" to firstname,              // Store firstname separately
                "lastname" to lastname,                // Store lastname separately
                "email" to email,
                "role" to role
            )

            db.collection("users").document(it.uid)
                .set(userDetails)
                .addOnSuccessListener {
                    println("User details saved successfully")
                }
                .addOnFailureListener { e ->
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

    // Add this function to fetch users
    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                db.collection("users")
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            val usersList = snapshot.documents.mapNotNull { doc ->
                                doc.toObject(User::class.java)?.copy(uid = doc.id)
                            }
                            _users.value = usersList
                        }
                    }
            } catch (e: Exception) {
                // Handle error
            }
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

