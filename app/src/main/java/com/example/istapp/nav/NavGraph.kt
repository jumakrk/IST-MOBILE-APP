package com.example.istapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.istapp.viewmodels.AuthViewModel
import com.example.istapp.screens.*

@Composable
fun NavGraph(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Routes.splashScreen
    ) {
        // Authentication routes
        composable(Routes.login) {
            LoginScreen(navController, authViewModel)
        }
        
        composable(Routes.signup) {
            SignupScreen(navController, authViewModel)
        }
        
        composable(Routes.forgotPassword) {
            ForgotPasswordScreen(navController, authViewModel)
        }
        
        composable(Routes.verificationEmailSent) {
            VerificationEmailSentScreen(navController, authViewModel)
        }

        // Main app routes
        composable(Routes.homepage) {
            HomeScreen(navController, authViewModel)
        }
        
        composable(Routes.profile) {
            ProfileScreen(navController, authViewModel)
        }
        
        composable(Routes.splashScreen) {
            AnimatedSplashScreen(navController)
        }
        
        // Jobs related routes
        composable(Routes.jobs) {
            JobsScreen(navController, authViewModel)
        }
        
        composable(Routes.postJob) {
            PostJobScreen(navController, authViewModel)
        }
        
        composable(Routes.viewJob) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            ViewJobScreen(
                jobId = jobId, 
                navController = navController,
                authViewModel = authViewModel
            )
        }
        
        // User management routes
        composable(Routes.viewUsers) { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "user"
            ViewUsersScreen(navController, authViewModel, userType)
        }

        composable(Routes.editJob) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            EditJobScreen(
                jobId = jobId,
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}