package com.example.istapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.istapp.AuthViewModel
import com.example.istapp.R


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(authViewModel: NavHostController, navController: AuthViewModel) {

    // TopAppBar scroll behavior for hiding/showing title when scrolling
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( state = rememberTopAppBarState())

    Scaffold(
        topBar = {
            TopBar(scrollBehavior = scrollBehavior)
        },
    ) {
        paddingValues -> HomeScreenContent(Modifier.padding(paddingValues))
    }
}

@Composable
fun HomeScreenContent(modifier: Modifier = Modifier) {}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier, scrollBehavior: TopAppBarScrollBehavior) {
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
            Text(
                text = "IST Alumni App",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu Icon",
                modifier = Modifier
                    .padding(start= 16.dp, end = 8.dp )
                    .size(27.dp)
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Menu Icon",
                modifier = Modifier
                    .padding(end = 8.dp )
                    .size(24.dp)
            )

            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Menu Icon",
                modifier = Modifier
                    .padding(start= 8.dp, end = 16.dp )
                    .size(30.dp),
            )
        },
    )
}
