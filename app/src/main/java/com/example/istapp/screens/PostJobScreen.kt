package com.example.istapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.istapp.viewmodels.AuthState
import com.example.istapp.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                TopBar(
                    navController = navController,
                    scrollBehavior = scrollBehavior,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                BottomBar(navController = navController)
            },
            content = { paddingValues ->
                PostJobForm(paddingValues = paddingValues)
            },
        )
    }
}

@Composable
fun PostJobForm(paddingValues: PaddingValues) {
    var jobTitle by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var postedBy by remember { mutableStateOf("") }
    var isSoftwareDevelopment by remember { mutableStateOf(false) }
    var isCyberSecurity by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    // Get current user
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Fetch username from Firestore when the component is first created
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        postedBy = document.getString("username") ?: ""
                    }
                }
        }
    }

    // Firestore instance
    val db = FirebaseFirestore.getInstance()

    // Button colors variable
    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Red,
        contentColor = Color.White
    )

    var jobTitleIsFocused by remember { mutableStateOf(false) }
    var companyIsFocused by remember { mutableStateOf(false) }
    var locationIsFocused by remember { mutableStateOf(false) }
    var descriptionIsFocused by remember { mutableStateOf(false) }
    var postedByIsFocused by remember { mutableStateOf(false) }
    var datePostedIsFocused by remember { mutableStateOf(false) }
    var applicationDeadlineIsFocused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    // Get the current date
    val datePosted = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    val authViewModel = AuthViewModel()
    val authState by authViewModel.authState.observeAsState(AuthState.Loading)

    // State for the application deadline
    var applicationDeadline by remember { mutableStateOf("") }

    // Date picker dialog for the application deadline
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Format the selected date
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                applicationDeadline =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
        OutlinedTextField(
            value = jobTitle,
            onValueChange = { jobTitle = it },
            label = {
                Text(
                    text = "Job Title",
                    color = if (jobTitleIsFocused) Color.Red else Color.Gray
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
                .focusRequester(focusRequester)
                .onFocusChanged { focusState -> jobTitleIsFocused = focusState.isFocused }
                .fillMaxWidth(),
        )
        }

        item {
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
                .focusRequester(focusRequester)
                .onFocusChanged { focusState -> companyIsFocused = focusState.isFocused }
                .fillMaxWidth(),
        )
        }

        item {
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
                .focusRequester(focusRequester)
                .onFocusChanged { focusState -> locationIsFocused = focusState.isFocused }
                .fillMaxWidth(),
        )
        }

        item {
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
                .focusRequester(focusRequester)
                .onFocusChanged { focusState -> descriptionIsFocused = focusState.isFocused }
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 100
        )
        }

        item {
            // Job type selection checkboxes
            Text("Job Type", color = Color.Gray) // Label for job type selection
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isSoftwareDevelopment,
                    onCheckedChange = { isSoftwareDevelopment = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Red)
                )
                Text(
                    text = "Software Development",
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.Gray
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isCyberSecurity,
                    onCheckedChange = { isCyberSecurity = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Red)
                )
                Text(
                    text = "Cyber Security",
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.Gray
                )
            }
        }

        item {
        OutlinedTextField(
            value = postedBy,
            onValueChange = { /* Read only */ },
            label = {
                Text(
                    text = "Posted By",
                    color = if (postedByIsFocused) Color.Red else Color.Gray
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
                .focusRequester(focusRequester)
                .onFocusChanged { focusState -> postedByIsFocused = focusState.isFocused }
                .fillMaxWidth(),
            readOnly = true // Make the field read-only
        )
        }

        item {
        // Display date posted, not editable
        OutlinedTextField(
            value = datePosted,
            onValueChange = {},
            label = {
                Text(
                    text = "Date Posted",
                    color = if (datePostedIsFocused) Color.Red else Color.Gray
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
                .focusRequester(focusRequester)
                .onFocusChanged { focusState -> datePostedIsFocused = focusState.isFocused }
                .fillMaxWidth(),
            readOnly = true
        )
        }

        item {
            // Display and pick the application deadline date
            OutlinedTextField(
                value = applicationDeadline,
                onValueChange = {},
                label = {
                    Text(
                        text = "Application Deadline",
                        color = if (applicationDeadlineIsFocused) Color.Red else Color.Gray
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
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState -> applicationDeadlineIsFocused = focusState.isFocused }
                    .fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick a date")
                    }
                }
            )
        }

        item {
        Button(
            onClick = {
                if (jobTitle.isNotEmpty() && company.isNotEmpty() && location.isNotEmpty() && description.isNotEmpty()) {

                    // Determine the job type
                    val jobType = when {
                        isSoftwareDevelopment -> "Software Development"
                        isCyberSecurity -> "Cyber Security"
                        else -> "Other" // Default value if neither is selected
                    }

                        // Show the loading indicator while uploading
                        isLoading = true
                    uploadJobToFirestore(jobTitle, company, location, description, jobType, postedBy, datePosted, applicationDeadline, context, db){
                        // Hide the loading indicator after successful upload
                        isLoading = false
                        // Clear the input fields after a successful post
                        jobTitle = ""
                        company = ""
                        location = ""
                        description = ""
                        postedBy = ""
                        applicationDeadline = ""
                        isSoftwareDevelopment = false // Reset job type selections when posting
                        isCyberSecurity = false
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = authState !is AuthState.Loading,
            colors = buttonColors,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = "Post Job")
            }
        }
        }
    }
}

private fun uploadJobToFirestore(jobTitle: String, company: String, location: String, description: String, jobType: String, postedBy: String, datePosted: String, applicationDeadline: String, context: Context, db: FirebaseFirestore, onSuccess: () -> Unit) {
    // Create job data
    val jobData = hashMapOf(
        "title" to jobTitle,
        "company" to company,
        "location" to location,
        "description" to description,
        "jobType" to jobType,
        "postedBy" to postedBy,
        "datePosted" to datePosted,
        "applicationDeadline" to applicationDeadline
    )

    // Save job data to Firestore
    db.collection("jobs")
        .add(jobData)
        .addOnSuccessListener {
            Toast.makeText(context, "Job posted successfully", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error posting job: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
