package com.example.istapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istapp.AuthState
import com.example.istapp.AuthViewModel
import com.example.istapp.R
import com.example.istapp.nav.Routes
import kotlinx.coroutines.delay

@Composable
fun VerificationEmailSentScreen(navController: NavController, authViewModel: AuthViewModel) {

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Red,
        contentColor = Color.White
    )

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    // State to manage button enable/disable and timer
    var isButtonEnabled by remember { mutableStateOf(true) }
    var timerText by remember { mutableStateOf("") }

    // Check the email verification status periodically
    LaunchedEffect(Unit) {
        while (true) {
            authViewModel.checkEmailVerificationStatus()
            delay(5000L) // Check every 5 seconds
        }
    }

    // LaunchedEffect to react to state changes and navigate or show messages
    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Authenticated -> {
                // Navigate to the homepage if email is verified
                Toast.makeText(context, "Email verified successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.homepage) {
                    popUpTo(Routes.verificationEmailSent) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                // Show error message
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    // Timer logic for resend email button
    LaunchedEffect(isButtonEnabled) {
        if (!isButtonEnabled) {
            var countdown = 60
            while (countdown > 0) {
                timerText = "Resend Email in $countdown seconds"
                delay(1000L)
                countdown--
            }
            isButtonEnabled = true
            timerText = "You can resend the email again."
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.ist_logo), contentDescription = "IST Logo",
            modifier = Modifier.size(150.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Verification Email Sent!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "A verification email has been sent to your email. Please check your inbox to verify your account.",
            fontSize = 16.sp,
            color = Color.Gray,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "Didn't receive the email? ",
                color = Color.Black,
                fontWeight = FontWeight.Bold)
        }

        Button(onClick = {
            if (isButtonEnabled) {
                authViewModel.resendVerificationEmail()
                isButtonEnabled = false // Disable button immediately after click
            }
        },
            colors = buttonColors,
            modifier = Modifier.width(150.dp),
            enabled = isButtonEnabled
        ) {
            Text(text = "Resend Email")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display the countdown timer text
        Text(text = timerText, color = Color.Gray, fontSize = 14.sp)
    }
}
