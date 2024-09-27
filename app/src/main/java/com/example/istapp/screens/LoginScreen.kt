package com.example.istapp.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istapp.AuthState
import com.example.istapp.AuthViewModel
import com.example.istapp.R
import com.example.istapp.nav.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Red,
        contentColor = Color.White
    )

    var passwordVisible by remember { mutableStateOf(false) }
    var passwordText by remember { mutableStateOf("") }
    var passwordIsFocused by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.observeAsState(AuthState.Loading)
    var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    var isSigningIn by remember { mutableStateOf(false) } // Track if google sign-in is in progress

    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            isSigningIn = false // Set to false when sign-in completes
            user = result.user
            Toast.makeText(context, "Welcome back! You are logged in with google as ${user?.email}.", Toast.LENGTH_SHORT).show()
            // Navigate to homepage only if the login with google was successful
            Log.d("LoginScreen", "Navigating to homepage")
            navController.navigate(Routes.homepage) {
                popUpTo(Routes.login) { inclusive = true }
            }
        },
        onAuthError = { exception ->
            isSigningIn = false // Set to false on error
            user = null
            Toast.makeText(context, "Authentication failed: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    )

    val token = stringResource(id = R.string.google_client_id)

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
            // Navigate to homepage on successful authentication
            navController.navigate(Routes.homepage) {
                popUpTo(Routes.login) { inclusive = true }
            }
        } else if (authState is AuthState.Error) {
            Toast.makeText(
                context,
                (authState as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    var email by remember { mutableStateOf("") }
    var emailIsFocused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester()}

    // For preventing action on Enter key
    fun handleKeyEvent(keyEvent: KeyEvent): Boolean {
        return when (keyEvent.key) {
            Key.Enter -> true // Prevent any action on Enter key
            else -> false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ist_logo),
            contentDescription = "IST Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome Back",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Please login to continue",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = {
                Text(
                    text = "Email",
                    color = if (emailIsFocused) Color.Red else Color.Gray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Red,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Red
            ),
            modifier = Modifier
                .width(300.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState -> emailIsFocused = focusState.isFocused}
                .onKeyEvent { handleKeyEvent(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val icon = if (passwordVisible) R.drawable.hide_icon else R.drawable.show_icon

        OutlinedTextField(
            value = passwordText,
            onValueChange = { passwordText = it.trim() },
            label = {
                Text(
                    text = "Password",
                    color = if (passwordIsFocused) Color.Red else Color.Gray
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Red,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Red
            ),
            modifier = Modifier
                .width(300.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState -> passwordIsFocused = focusState.isFocused }
                .onKeyEvent { handleKeyEvent(it) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { passwordVisible = !passwordVisible }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.login(email, passwordText)
            },
            enabled = authState !is AuthState.Loading,
            colors = buttonColors,
            modifier = Modifier.width(120.dp)
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    color = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = "Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Forgot Password?",
            Modifier.clickable { navController.navigate(Routes.forgotPassword) },
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(
                text = "Don't have an account? ",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Sign Up",
                Modifier.clickable { navController.navigate(Routes.signup){
                    popUpTo(Routes.login) { inclusive = true } // Clear the back stack(previous screens to prevent going back to them when clicking "Back")
                } },
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "or",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Continue with",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (isSigningIn) {
                CircularProgressIndicator( // Show progress indicator while signing in with google
                    color = Color.Red,
                    modifier = Modifier.size(30.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            isSigningIn = true // Set to true when sign-in starts
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(token)
                                .requestEmail()
                                .build()

                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            launcher.launch(googleSignInClient.signInIntent)
                        }
                )
            }
        }
    }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()

    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("GoogleAuth", "Account: $account")

            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                try {
                    val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
                    Log.d("GoogleAuth", "signInWithCredential:success")
                    onAuthComplete(authResult)
                } catch (e: Exception) {
                    Log.w("GoogleAuth", "signInWithCredential:failure", e)
                    onAuthError(e as ApiException)
                }
            }
        } catch (e: ApiException) {
            Log.d("GoogleAuth", "Google sign in failed", e)
            onAuthError(e)
        }
    }
}
