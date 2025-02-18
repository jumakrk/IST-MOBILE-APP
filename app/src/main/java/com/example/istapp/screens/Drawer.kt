package com.example.istapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.People
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.istapp.AuthViewModel
import com.example.istapp.R
import com.example.istapp.nav.Routes
import com.google.firebase.auth.FirebaseAuth

// Drawer content
@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    // Function to get the current user's username (display name) from Firebase Authentication
    fun getUsername(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName // Returns the username (displayName) or null if not set
    }

    val username = getUsername() // Get the current user's username

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Center the logo
        Image(
            painter = painterResource(R.drawable.ist_logo),
            contentDescription = "IST Logo",
            modifier = Modifier
                .size(100.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // IST Alumni text
        Text(
            text = "Hello, $username",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        HorizontalDivider()

        Spacer(modifier = Modifier.height(4.dp))

        // Observe the user's role from the ViewModel
        val userRole = authViewModel.userRole.observeAsState().value ?: "user"
        if (userRole == "admin") {
        // Navigation items
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.AdminPanelSettings,
                    contentDescription = "Admins",
                    modifier = Modifier.size(27.dp)
                )
            },
            label = { Text(text = "Admins", fontSize = 17.sp) },
            selected = false,
            onClick = {
                navController.navigate("viewUsers/admin")
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.People,
                    contentDescription = "Users",
                    modifier = Modifier.size(27.dp)
                )
            },
            label = { Text(text = "Users", fontSize = 17.sp) },
            selected = false,
            onClick = {
                navController.navigate("viewUsers/user")
            }
        )
        }

        // Spacer to push the logout button to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // Logout button at the bottom
        HorizontalDivider()

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Logout",
                    modifier = Modifier.size(27.dp)
                )
            },
            label = { Text(text = "Logout", fontSize = 17.sp) },
            selected = false,
            onClick = {
                authViewModel.logout(context = navController.context) // Log the user out of Firebase
                navController.navigate(Routes.login) {
                    popUpTo(Routes.homepage) { inclusive = true } // Clear the backstack
                }
            }
        )
    }
}