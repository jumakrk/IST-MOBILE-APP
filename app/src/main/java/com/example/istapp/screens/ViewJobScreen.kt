package com.example.istapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.istapp.AuthViewModel
import com.example.istapp.JobViewModel
import com.example.istapp.nav.Routes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewJobScreen(jobId: String, navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val viewModel: JobViewModel = viewModel()
    val jobDetails by viewModel.getJobDetails(jobId).collectAsState(initial = null)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())
    
    // Observe user role
    val userRole = authViewModel.userRole.observeAsState().value ?: "user"

    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    // State for delete progress
    var isDeleting by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(250.dp)) {
                DrawerContent(modifier = Modifier, navController = navController, authViewModel = authViewModel, onCloseDrawer = { scope.launch { drawerState.close() } })
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = jobDetails?.title ?: "Loading...",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (userRole == "admin") {
                            IconButton(
                                onClick = { showDeleteDialog = true }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Job",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Red,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                BottomBar(navController = navController)
            }
        ) { paddingValues ->
            jobDetails?.let { job ->
                // Check if application deadline has passed
                val isDeadlinePassed = remember(job.applicationDeadline) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    try {
                        val deadline = dateFormat.parse(job.applicationDeadline)
                        val today = Calendar.getInstance().time
                        deadline?.before(today) ?: true
                    } catch (e: Exception) {
                        true
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Company
                        Text(
                            text = job.company,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    item {
                        // Location
                        Text(
                            text = "📍 ${job.location}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        // Job Type
                        Text(
                            text = "Type: ${job.type}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        // Description Header
                        Text(
                            text = "Description",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Description Content
                        Text(
                            text = job.description,
                            fontSize = 16.sp
                        )
                    }

                    item {
                        // Additional Information
                        Text(
                            text = "Additional Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                    Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Posted by: ${job.postedBy}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "Posted on: ${job.datePosted}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "Application Deadline: ${job.applicationDeadline}",
                            fontSize = 14.sp,
                            color = if (isDeadlinePassed) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                    // Apply Button
                    Button(
                            onClick = { /* Handle apply action */ },
                        modifier = Modifier
                            .fillMaxWidth()
                                .height(50.dp)
                                .padding(vertical = 8.dp),
                            enabled = !isDeadlinePassed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (isDeadlinePassed) "Application Deadline Passed" else "Apply",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            } ?: run {
                // Show loading indicator in the content area while keeping nav elements visible
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Red)
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!isDeleting) showDeleteDialog = false 
            },
            title = { Text("Delete Job") },
            text = { 
                if (isDeleting) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Red
                        )
                        Text("Deleting job...")
                    }
                } else {
                    Text("Are you sure you want to delete this job?")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!isDeleting) {
                            isDeleting = true
                            scope.launch {
                                try {
                                    viewModel.deleteJob(jobId)
                                    Toast.makeText(context, "Job deleted successfully", Toast.LENGTH_SHORT).show()
                                    showDeleteDialog = false
                                    navController.navigate(Routes.jobs) {
                                        popUpTo(Routes.jobs) { inclusive = true }
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Error deleting job: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isDeleting = false
                                }
                            }
                        }
                    },
                    enabled = !isDeleting
                ) {
                    Text("Delete", color = if (!isDeleting) Color.Red else Color.Gray)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    enabled = !isDeleting
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
