package com.example.istapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.istapp.nav.NavGraph
import com.example.istapp.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize the AuthViewModel
        val authViewModel : AuthViewModel by viewModels()
        setContent {
           NavGraph(authViewModel = authViewModel)
        }
    }
}