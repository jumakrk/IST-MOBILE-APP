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
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.navigation.NavController
import com.example.istapp.nav.Routes

@Composable
fun BottomBar(
    navController: NavController,
    onNavigate: () -> Unit = {} // Add default value for backward compatibility
) {
    // Determine selected index based on the current route
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val selectedIndex = bottomNavItems.indexOfFirst { it.route == currentRoute }

    // Selected item state
    var selected by remember { mutableIntStateOf(selectedIndex.takeIf { it != -1 } ?: 0) }

    Box(
        modifier = Modifier.height(85.dp)
    ) {
        NavigationBar(
            containerColor = Color.Red,
        ) {
            bottomNavItems.forEachIndexed { index, bottomNavItem ->
                NavigationBarItem(
                    selected = index == selected,
                    onClick = {
                        onNavigate() // Call onNavigate before navigation
                        if (selected != index) {
                            selected = index
                            navController.navigate(bottomNavItem.route) {
                                popUpTo(Routes.homepage) // Pop up to homepage
                                launchSingleTop = true
                            }
                        } else if (index == 0) {
                            // If home is already selected, pop back to homepage
                            navController.navigate(Routes.homepage) {
                                popUpTo(Routes.homepage) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (bottomNavItem.badges != 0 || bottomNavItem.hasNews) {
                                    Badge {
                                        if (bottomNavItem.badges != 0) {
                                            Text(
                                                text = bottomNavItem.badges.toString(),
                                                color = Color.White // Change text color if needed
                                            )
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector =
                                if (index == selected)
                                    bottomNavItem.selectedIcon
                                else
                                    bottomNavItem.unselectedIcon,
                                contentDescription = bottomNavItem.label,
                                tint = if (index == selected) Color.White else Color.LightGray // Change icon color based on selection
                            )
                        }
                    },
                    label = {
                        Text(
                            text = bottomNavItem.label,
                            color = if (index == selected) Color.White else Color.LightGray // Change text color based on selection
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White, // Icon color when selected
                        unselectedIconColor = Color.LightGray,    // Icon color when unselected
                        indicatorColor = Color.Transparent // Set indicator color to transparent
                    ),
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
        route = Routes.jobs, //TODO: Change to jobs screen
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
