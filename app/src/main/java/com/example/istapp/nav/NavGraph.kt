package com.example.istapp.nav


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.istapp.AuthViewModel
import com.example.istapp.screens.AnimatedSplashScreen
import com.example.istapp.screens.ForgotPasswordScreen
import com.example.istapp.screens.HomeScreen
import com.example.istapp.screens.JobsScreen
import com.example.istapp.screens.LoginScreen
import com.example.istapp.screens.SignupScreen
import com.example.istapp.screens.VerificationEmailSentScreen

@Composable
fun NavGraph(authViewModel: AuthViewModel){
    val navController = rememberNavController() //Initializing navController
    NavHost(navController = navController, startDestination = Routes.splashScreen, builder = {
        composable(Routes.login, content = {
            LoginScreen(navController, authViewModel)
        })
        composable(Routes.signup, content = {
            SignupScreen(navController, authViewModel)
        })
        composable(Routes.forgotPassword, content = {
            ForgotPasswordScreen(navController, authViewModel)
        })
        composable(Routes.homepage, content = {
            HomeScreen(navController, authViewModel)
        })
        composable(Routes.verificationEmailSent, content = {
            VerificationEmailSentScreen(navController, authViewModel)
        })
        composable(Routes.splashScreen, content = {
            AnimatedSplashScreen(navController)
        })
        composable(Routes.jobs, content = {
            JobsScreen(navController, authViewModel)
        })
    })
}