package com.example.istapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
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
    authViewModel: AuthViewModel,
    onCloseDrawer: () -> Unit
) {
    // Function to get the current user's username (display name) from Firebase Authentication
    fun getUsername(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName
    }

    val username = getUsername()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 12.dp)
    ) {
        // Clickable logo for closing drawer
        Image(
            painter = painterResource(R.drawable.ist_logo),
            contentDescription = "IST Logo",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onCloseDrawer)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Welcome text
        Text(
            text = "Hello, $username",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Observe the user's role from the ViewModel
        val userRole = authViewModel.userRole.observeAsState().value ?: "user"
        if (userRole == "admin") {
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.AdminPanelSettings,
                        contentDescription = "Admins",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = "Admins", fontSize = 16.sp) },
                selected = false,
                onClick = {
                    navController.navigate("viewUsers/admin")
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.People,
                        contentDescription = "Users",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(text = "Users", fontSize = 16.sp) },
                selected = false,
                onClick = {
                    navController.navigate("viewUsers/user")
                    onCloseDrawer()
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Logout",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(text = "Logout", fontSize = 16.sp) },
            selected = false,
            onClick = {
                authViewModel.logout(context = navController.context)
                navController.navigate(Routes.login) {
                    popUpTo(Routes.homepage) { inclusive = true }
                }
            }
        )
    }
}