package com.example.istapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.istapp.screens.ForgotPasswordScreen
import com.example.istapp.screens.LoginScreen
import com.example.istapp.screens.SignupScreen

@Composable
fun NavGraph(){
    val navController = rememberNavController() //Initializing navController
    NavHost(navController = navController, startDestination = Routes.login, builder = {
        composable(Routes.login, content = {
            LoginScreen(navController)
        })
        composable(Routes.signup, content = {
            SignupScreen(navController)
        })
        composable(Routes.forgotPassword, content = {
            ForgotPasswordScreen(navController)
        })
    })
}