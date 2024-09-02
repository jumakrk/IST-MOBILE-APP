package com.example.istapp.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.istapp.AuthViewModel
import com.example.istapp.screens.ForgotPasswordScreen
import com.example.istapp.screens.HomeScreen
import com.example.istapp.screens.LoginScreen
import com.example.istapp.screens.SignupScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController() //Initializing navController
    NavHost(navController = navController, startDestination = Routes.login, builder = {
        composable(Routes.login, content = {
            LoginScreen(modifier, navController, authViewModel)
        })
        composable(Routes.signup, content = {
            SignupScreen(navController, authViewModel)
        })
        composable(Routes.forgotPassword, content = {
            ForgotPasswordScreen(modifier, navController, authViewModel)
        })
        composable(Routes.homepage, content = {
            HomeScreen(navController, authViewModel)
        })
    })
}