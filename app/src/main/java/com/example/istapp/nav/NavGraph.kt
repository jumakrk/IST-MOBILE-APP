package com.example.istapp.nav


import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.istapp.AuthViewModel
import com.example.istapp.screens.AnimatedSplashScreen
import com.example.istapp.screens.ForgotPasswordScreen
import com.example.istapp.screens.HomeScreen
import com.example.istapp.screens.JobsScreen
import com.example.istapp.screens.LoginScreen
import com.example.istapp.screens.PostJobScreen
import com.example.istapp.screens.SignupScreen
import com.example.istapp.screens.VerificationEmailSentScreen
import com.example.istapp.screens.ViewJobScreen

@OptIn(ExperimentalMaterial3Api::class)
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
        composable(Routes.postJob, content = {
            PostJobScreen(navController, authViewModel)
        })
        composable(
            route = Routes.viewJob,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: return@composable
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())
            val scope = rememberCoroutineScope()
            val userRole = authViewModel.userRole.value ?: "user"
            ViewJobScreen(
                navController = navController,
                jobId = jobId,
                authViewModel = authViewModel,
                drawerState = drawerState,
                scrollBehavior = scrollBehavior,
                scope = scope,
                userRole = userRole
            )
        }

    })
}