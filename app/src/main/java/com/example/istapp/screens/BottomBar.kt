package com.example.istapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.istapp.nav.Routes

@Composable
fun BottomBar(navController: NavHostController) {

    // Selected item state
    var selected by remember { mutableIntStateOf(0) }

    // BottomBar content
    Box(
        modifier = Modifier
            .height(85.dp)
    ) {
        NavigationBar (
            containerColor = Color.Red,
        ) {
            bottomNavItems.forEachIndexed { index, bottomNavItem ->
                NavigationBarItem(
                    selected = index == selected,
                    onClick = {
                        selected = index
                        navController.navigate(bottomNavItem.route)
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                //Check if the item has a badge and show the Badge for the notification
                                if (bottomNavItem.badges != 0) {
                                    Badge {
                                        Text(
                                            text = bottomNavItem.badges.toString()
                                        )
                                    }
                                } else if (bottomNavItem.hasNews) {
                                    Badge()
                                }
                            }
                        ) {
                            // Show the icon for the item
                            Icon(
                                imageVector =
                                if (index == selected)
                                    bottomNavItem.selectedIcon
                                else
                                    bottomNavItem.unselectedIcon,
                                contentDescription = bottomNavItem.label,
                                tint = Color.LightGray
                            )
                        }
                    },
                    label = { Text(text = bottomNavItem.label) }
                )
            }
        }
    }
}

// Navigation items
val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        route = Routes.homepage,
        icon = Icons.Rounded.Home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Rounded.Home,
        hasNews = false,
        badges = 0,
    ),

    BottomNavItem(
        label = "Jobs",
        route = Routes.login, //TODO: Change to jobs screen
        icon = Icons.Rounded.BusinessCenter,
        selectedIcon = Icons.Filled.BusinessCenter,
        unselectedIcon = Icons.Rounded.BusinessCenter,
        hasNews = false,
        badges = 0,
    ),
)

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badges: Int,
    val hasNews: Boolean,
    )