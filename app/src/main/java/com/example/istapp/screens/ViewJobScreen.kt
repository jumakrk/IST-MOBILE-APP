package com.example.istapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.istapp.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewJobScreen(jobId: String, navController: NavController) {
    // Get the JobViewModel instance
    val viewModel: JobViewModel = viewModel()

    // Fetch job details from Firestore
    val jobDetails by viewModel.getJobDetails(jobId).collectAsState(initial = null)

    // Check if job details are available
    jobDetails?.let { job ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = job.title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.deleteJob(jobId)
                            navController.popBackStack() // Navigate back after deletion
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Job Details
                    Text(text = "Location: ${job.location}")
                    Text(text = "Type: ${job.type}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Description:")
                    Text(text = job.description)

                    // Apply Button
                    Button(
                        onClick = { /* Handle apply action here */ },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(text = "Apply")
                    }
                }
            }
        )
    } ?: run {
        // Show loading indicator or error message if job details are null
        CircularProgressIndicator()
    }
}
