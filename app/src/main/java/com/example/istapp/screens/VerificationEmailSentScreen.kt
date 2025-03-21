package com.example.istapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istapp.viewmodels.AuthState
import com.example.istapp.viewmodels.AuthViewModel
import com.example.istapp.R
import com.example.istapp.nav.Routes
import kotlinx.coroutines.delay

@Composable
fun VerificationEmailSentScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val authState by authViewModel.authState.observeAsState()
    var timerText by remember { mutableStateOf("") }
    var canResend by remember { mutableStateOf(false) }

    // Periodically check email verification status
    LaunchedEffect(Unit) {
        while (true) {
            authViewModel.checkEmailVerificationStatus()
            delay(3000) // Check every 3 seconds
        }
    }

    // LaunchedEffect to react to state changes and navigate or show messages
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                Toast.makeText(context, "Email verified successfully! Welcome!", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.homepage) {
                    popUpTo(Routes.verificationEmailSent) { inclusive = true }
                }
            }
            is AuthState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    // Timer logic for resend email button
    LaunchedEffect(Unit) {
        var countdown = 60
        while (countdown > 0) {
            timerText = "Resend Email in $countdown seconds"
            canResend = false
            delay(1000L)
            countdown--
        }
        timerText = "Resend Verification Email"
        canResend = true
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
            text = "Verification Email Sent!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "A verification email has been sent to your email address.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please check your inbox and click the verification link.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (canResend) {
                    authViewModel.resendVerificationEmail()
                }
            },
            enabled = canResend,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray
            ),
            modifier = Modifier.width(250.dp)
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(text = timerText)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already verified? ",
                color = Color.Gray
            )
            Text(
                text = "Login here",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(Routes.login) {
                        popUpTo(Routes.verificationEmailSent) { inclusive = true }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Note: If you don't see the email, please check your spam folder.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}