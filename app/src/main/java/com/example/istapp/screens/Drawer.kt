package com.example.istapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.istapp.R

// Drawer content
@Composable
fun DrawerContent(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ist_logo),
        contentDescription = "IST Logo",
        modifier = modifier
            .size(100.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "IST Alumni",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(16.dp)
        )

    HorizontalDivider()

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "Users",
                modifier = modifier.size(27.dp)
            )
        },
        label = {
            Text(
            text = "Users",
            fontSize = 17.sp
        )},
        selected = false,
        onClick = {}
    )

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = "Roles",
                modifier = modifier.size(27.dp)
            )
        },
        label = {
            Text(
                text = "Roles",
                fontSize = 17.sp
            )},
        selected = false,
        onClick = {}
    )

    Spacer(modifier = Modifier.height(4.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = "Permissions",
                modifier = modifier.size(27.dp)
            )
        },
        label = {
            Text(
                text = "Permissions",
                fontSize = 17.sp
            )},
        selected = false,
        onClick = {}
    )
}