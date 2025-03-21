package com.example.istapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istapp.viewmodels.AuthViewModel
import com.example.istapp.R
import com.example.istapp.nav.Routes
import com.google.firebase.auth.FirebaseAuth

// Drawer content
@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    onCloseDrawer: () -> Unit
) {
    // Function to get the current user's username (display name) from Firebase Authentication
    fun getUsername(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName
    }

    val username = getUsername()
    val userRole = authViewModel.userRole.observeAsState().value ?: "user"

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color.White)
            .padding(top = 16.dp)
    ) {
        // Back arrow and logo section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Back arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Close Drawer",
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(24.dp)
                    .clickable(onClick = onCloseDrawer)
            )

            // Logo centered
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.ist_logo),
                    contentDescription = "IST Logo",
                    modifier = Modifier.size(100.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = username ?: "User",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = userRole.replaceFirstChar { it.uppercase() },
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Items
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            if (userRole == "admin") {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.AdminPanelSettings,
                            contentDescription = "Admins",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Red
                        )
                    },
                    label = { Text(text = "Manage Admins", fontSize = 16.sp) },
                    selected = false,
                    onClick = {
                        navController.navigate("viewUsers/admin")
                        onCloseDrawer()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.People,
                            contentDescription = "Users",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Red
                        )
                    },
                    label = { Text(text = "Manage Users", fontSize = 16.sp) },
                    selected = false,
                    onClick = {
                        navController.navigate("viewUsers/user")
                        onCloseDrawer()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        selectedContainerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        // Logout button at bottom
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Logout",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Red
                )
            },
            label = { Text(text = "Logout", fontSize = 16.sp) },
            selected = false,
            onClick = {
                authViewModel.logout(context = navController.context)
                navController.navigate(Routes.login) {
                    popUpTo(Routes.homepage) { inclusive = true }
                }
            },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                selectedContainerColor = Color.Red.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(8.dp)
        )
    }
}