package com.example.istapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.istapp.AuthViewModel


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
        paddingValues -> HomeScreenContent(paddingValues = paddingValues)
    }
}

@Composable
fun HomeScreenContent(paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
        )
    ) {
        items(10){
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
