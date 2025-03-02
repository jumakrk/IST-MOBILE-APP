package com.example.istapp.screens

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    var isConnected by remember { mutableStateOf(false) }
    var showRetryButton by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        label = "SplashAnimation",
        animationSpec = tween(
            durationMillis = 2000
        )
    )

    LaunchedEffect(key1 = showRetryButton) {
        startAnimation = true
        delay(3000) // Delay to show the splash screen animation

        if (checkInternetConnection(context)) {
            isConnected = true
            showRetryButton = false
            val user = FirebaseAuth.getInstance().currentUser
            navHostController.popBackStack()
            if (user == null) {
                navHostController.navigate("login")
            } else {
                navHostController.navigate("homepage")
            }
        } else {
            isConnected = false
            showRetryButton = true
        }
    }

    SplashView(
        alpha = alphaAnimation.value,
        showRetryButton = showRetryButton,
        onRetryClick = {
            showRetryButton = false
            startAnimation = false
            startAnimation = true
        }
    )
}

@Composable
fun SplashView(
    alpha: Float,
    showRetryButton: Boolean,
    onRetryClick: () -> Unit
) {
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

        // Align the loading indicator, text, or retry button at the bottom center
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showRetryButton) {
                // Display the "No Internet Connection" text and Retry button
                Text(
                    text = "No Internet Connection",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    modifier = Modifier.alpha(alpha)
                )

                Spacer(modifier = Modifier.height(8.dp))

                //Variable for button colors for the retry button
                val buttonColors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )

                // Retry Button
                Button(
                    onClick = { onRetryClick() },
                    colors = buttonColors
                ) {
                    Text(text = "Retry")
                }
            } else {
                // Display the "Loading" text and CircularProgressIndicator
                Text(
                    text = "Loading",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.alpha(alpha)
                )

                Spacer(modifier = Modifier.height(8.dp))

                CircularProgressIndicator(
                    modifier = Modifier.alpha(alpha),
                    color = Color.Red,
                    strokeWidth = 5.dp
                )
            }
        }
    }
}

// Function to check internet connection
fun checkInternetConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
@Preview
fun SplashScreenPreview() {
    SplashView(alpha = 1f, showRetryButton = false, onRetryClick = {})
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun SplashScreenDarkPreview() {
    SplashView(alpha = 1f, showRetryButton = false, onRetryClick = {})
}
