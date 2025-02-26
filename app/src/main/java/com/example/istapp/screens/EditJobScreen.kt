package com.example.istapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.istapp.AuthViewModel
import com.example.istapp.JobViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJobScreen(
    jobId: String,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val jobViewModel: JobViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var applicationDeadline by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var titleIsFocused by remember { mutableStateOf(false) }
    var companyIsFocused by remember { mutableStateOf(false) }
    var locationIsFocused by remember { mutableStateOf(false) }
    var descriptionIsFocused by remember { mutableStateOf(false) }
    var deadlineIsFocused by remember { mutableStateOf(false) }

    // Add the DatePicker dialog
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Format the selected date
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                applicationDeadline = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
    }

    // Fetch existing job details
    LaunchedEffect(jobId) {
        jobViewModel.getJobDetails(jobId).collect { job ->
            if (job != null) {
                title = job.title
                company = job.company
                location = job.location
                description = job.description
                applicationDeadline = job.applicationDeadline
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(250.dp)) {
                DrawerContent(
                    modifier = Modifier,
                    navController = navController,
                    authViewModel = authViewModel,
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
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
                BottomBar(
                    navController = navController,
                    onNavigate = { 
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = {
                            Text(
                                text = "Job Title",
                                color = if (titleIsFocused) Color.Red else Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Red
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState -> titleIsFocused = focusState.isFocused }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = {
                            Text(
                                text = "Company Name",
                                color = if (companyIsFocused) Color.Red else Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Red
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState -> companyIsFocused = focusState.isFocused }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = {
                            Text(
                                text = "Location",
                                color = if (locationIsFocused) Color.Red else Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Red
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState -> locationIsFocused = focusState.isFocused }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = {
                            Text(
                                text = "Description",
                                color = if (descriptionIsFocused) Color.Red else Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Red
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState -> descriptionIsFocused = focusState.isFocused },
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = applicationDeadline,
                        onValueChange = { applicationDeadline = it },
                        label = {
                            Text(
                                text = "Application Deadline",
                                color = if (deadlineIsFocused) Color.Red else Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Red,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Red
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState -> deadlineIsFocused = focusState.isFocused },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Pick a date",
                                    tint = if (deadlineIsFocused) Color.Red else Color.Gray
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                try {
                                    jobViewModel.updateJob(
                                        jobId,
                                        title,
                                        company,
                                        location,
                                        description,
                                        applicationDeadline
                                    )
                                    Toast.makeText(context, "Job updated successfully", Toast.LENGTH_SHORT).show()
                                    navController.navigateUp()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error updating job: ${e.message}", Toast.LENGTH_LONG).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Update Job")
                        }
                    }
                }
            }
        }
    }
} 