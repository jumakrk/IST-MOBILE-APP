package com.example.istapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istapp.nav.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, modifier: Modifier = Modifier, scrollBehavior: TopAppBarScrollBehavior, onOpenDrawer: () -> Unit) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    // TopAppBar with title, navigation icon, and action icons
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Red,
            titleContentColor = Color.LightGray,
            navigationIconContentColor = Color.LightGray,
            actionIconContentColor = Color.LightGray,
        ),
        title = {
            // Display the title based on the current route
            when (currentRoute) {
                Routes.jobs -> {
                    Text(
                        text = "Jobs",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Routes.profile -> {
                    Text(
                        text = "Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Routes.postJob -> {
                    Text(
                        text = "Post Job",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Routes.viewJob -> {
                    Text(
                        text = "View Job",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
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

            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .clickable{navController.navigate(Routes.profile){
                        popUpTo(Routes.profile) { inclusive = true } // Remove any previous instances of profile
                        launchSingleTop = true // Prevents reloading the same screen
                    } }
                    .padding(start = 8.dp, end = 16.dp)
                    .size(30.dp),
            )
        },
    )
}