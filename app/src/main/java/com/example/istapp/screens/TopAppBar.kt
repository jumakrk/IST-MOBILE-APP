package com.example.istapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istapp.nav.Routes
import com.example.istapp.viewmodels.ProfileViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    onOpenDrawer: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val userProfile by profileViewModel.userProfile.collectAsState()

    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Red,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White,
        ),
        title = {
            // Display the title based on the current route
            when (currentRoute) {
                Routes.jobs -> Text(text = "Jobs", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Routes.profile -> Text(text = "Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Routes.postJob -> Text(text = "Post Job", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Routes.viewJob -> Text(text = "View Job", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu Icon",
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .size(27.dp)
                    .clickable { onOpenDrawer() },
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Notification Icon",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            )

            Box(
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable {
                        navController.navigate(Routes.profile) {
                            popUpTo(Routes.profile) { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userProfile.username.firstOrNull()?.uppercase() ?: "U",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}