package com.example.istapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istapp.AuthViewModel
import com.example.istapp.R
import com.example.istapp.nav.Routes
import com.google.firebase.auth.FirebaseAuth


@Composable
fun HomeScreen(navController: NavController, authViewModel: AuthViewModel) {

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Red,
        contentColor = Color.White
    )

    val currentUser = FirebaseAuth.getInstance().currentUser // Get the current user

    LaunchedEffect(currentUser) {
        // If the user is not signed in, navigate to the login screen
        if (currentUser == null) {
            navController.navigate(Routes.login) {
                popUpTo(Routes.homepage) { inclusive = true } // Clear the backstack
            }
        } else if (!currentUser.isEmailVerified) {
            // If email is not verified, navigate to an email verification page
            navController.navigate(Routes.verificationEmailSent) {
                popUpTo(Routes.homepage) { inclusive = true } // Clear the backstack
            }
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
            text = "Welcome to IST App",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate(Routes.login) {
                    popUpTo(Routes.homepage) { inclusive = true } // Clear the backstack/Screens Before
                }
            },
            colors = buttonColors,
            modifier = Modifier.width(120.dp)
        ) {
            Text(text = "Log Out")
        }
    }
}
