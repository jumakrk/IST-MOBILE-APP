package com.example.istapp.screens

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier, scrollBehavior: TopAppBarScrollBehavior) {
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
                    .padding(start = 16.dp, end = 8.dp)
                    .size(27.dp)
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Menu Icon",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            )

            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Menu Icon",
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(30.dp),
            )
        },
    )
}