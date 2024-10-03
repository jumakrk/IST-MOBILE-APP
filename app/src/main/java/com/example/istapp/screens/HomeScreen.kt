package com.example.istapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.istapp.AuthViewModel
import com.example.istapp.nav.Routes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {

    // TopAppBar scroll behavior for hiding/showing title when scrolling
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())

    // Drawer state for the navigation drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(250.dp)) { // Set the width of the drawer
                DrawerContent(modifier = Modifier , navController = navController, authViewModel = authViewModel)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    scrollBehavior = scrollBehavior,
                    onOpenDrawer = { scope.launch { drawerState.open() } } // Open drawer on icon click
                )
            },
            content = { paddingValues ->
                HomeScreenContent(
                    paddingValues = paddingValues,
                )
            }
        )
    }

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
}

@Composable
fun HomeScreenContent(
    paddingValues: PaddingValues,
) {

// Variable to set the button colors
//    val buttonColors = ButtonDefaults.buttonColors(
//        containerColor = Color.Red,
//        contentColor = Color.White
//    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
        )
    ) {
        items(10) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
