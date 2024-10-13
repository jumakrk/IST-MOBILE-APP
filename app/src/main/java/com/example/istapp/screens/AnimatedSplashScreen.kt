package com.example.istapp.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.istapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(navHostController: NavHostController) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        label = "SplashAnimation",
        animationSpec = tween(
            durationMillis = 2000
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000) // Delay to show the splash screen animation

        val user = FirebaseAuth.getInstance().currentUser
        navHostController.popBackStack()
        if (user == null) {
            navHostController.navigate("login")
        } else {
            navHostController.navigate("homepage")
        }
    }

    SplashView(alpha = alphaAnimation.value)
}

@Composable
fun SplashView(alpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) Color.Black else Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Display the logo in the center
        Image(
            modifier = Modifier
                .alpha(alpha = alpha)
                .size(200.dp),
            painter = painterResource(id = R.drawable.ist_logo),
            contentDescription = "IST Logo"
        )

        // Align the loading indicator and text at the bottom center
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the "Loading" text
            Text(
                text = "Loading",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.alpha(alpha)
            )

            // Display the CircularProgressIndicator below the text
            Spacer(modifier = Modifier.height(8.dp)) // Space between text and progress indicator
            CircularProgressIndicator(
                modifier = Modifier.alpha(alpha),
                color = Color.Red,
                strokeWidth = 5.dp
            )
        }
    }
}

@Composable
@Preview
fun SplashScreenPreview() {
    SplashView(alpha = 1f)
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun SplashScreenDarkPreview() {
    SplashView(alpha = 1f)
}
