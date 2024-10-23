package com.example.istapp.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.istapp.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewJobScreen(
    navController: NavHostController,
    jobId: String,
    drawerState: DrawerState,
    scrollBehavior: TopAppBarScrollBehavior,
    scope: CoroutineScope,
    userRole: String,
    authViewModel: AuthViewModel,
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val context = LocalContext.current
    var jobData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isDeadlinePassed by remember { mutableStateOf(false) }

    // Fetch job details from Firestore
    LaunchedEffect(jobId) {
        try {
            val jobSnapshot = db.collection("jobs").document(jobId).get().await()
            jobData = jobSnapshot.data
            Log.d("ViewJobScreen", "Fetched job data: $jobData")
            isLoading = false

            // Check if the deadline has passed
            jobData?.get("applicationDeadline")?.let {
                val deadlineDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it as String)
                isDeadlinePassed = deadlineDate?.before(Date()) ?: false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to fetch job details: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(250.dp)) {
                DrawerContent(modifier = Modifier, navController = navController, authViewModel = authViewModel)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    navController = navController,
                    scrollBehavior = scrollBehavior,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                BottomBar(navController = navController)
            },
            floatingActionButton = {
                if (userRole == "admin") {
                    FloatingActionButton(
                        onClick = { navController.navigate("postJob") },
                        shape = RoundedCornerShape(40),
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Add Job"
                        )
                    }
                }
            },
            content = { paddingValues ->
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .size(48.dp),
                        color = Color.Red,
                    )
                } else {
                    ViewJobContent(
                        jobData = jobData,
                        isDeadlinePassed = isDeadlinePassed,
                        paddingValues = paddingValues,
                        navController = navController,
                        jobId = jobId,
                        context = context,
                        db = db
                    )
                }
            }
        )
    }
}

@Composable
fun ViewJobContent(
    jobData: Map<String, Any>?,
    isDeadlinePassed: Boolean,
    paddingValues: PaddingValues,
    navController: NavHostController,
    jobId: String,
    context: Context,
    db: FirebaseFirestore
) {
    jobData?.let { job ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Job Title: ${job["title"]}", style = MaterialTheme.typography.titleLarge)
            }
            item {
                Text("Company: ${job["company"]}")
            }
            item {
                Text("Location: ${job["location"]}")
            }
            item {
                Text("Description: ${job["description"]}")
            }
            item {
                Text("Job Type: ${job["jobType"]}")
            }
            item {
                Text("Posted By: ${job["postedBy"]}")
            }
            item {
                Text("Date Posted: ${job["datePosted"]}")
            }
            item {
                Text("Application Deadline: ${job["applicationDeadline"]}")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { /* Handle apply action */ },
                        enabled = !isDeadlinePassed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDeadlinePassed) Color.Gray else Color.Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            deleteJobFromFirestore(jobId, context, db) {
                                Toast.makeText(context, "Job deleted successfully", Toast.LENGTH_SHORT).show()
                                navController.popBackStack() // Navigate back after deletion
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

private fun deleteJobFromFirestore(
    jobId: String,
    context: Context,
    db: FirebaseFirestore,
    onSuccess: () -> Unit
) {
    db.collection("jobs").document(jobId)
        .delete()
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error deleting job: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
