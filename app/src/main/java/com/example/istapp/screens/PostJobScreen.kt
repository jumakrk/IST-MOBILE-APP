package com.example.istapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.istapp.AuthState
import com.example.istapp.AuthViewModel
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
    val context = LocalContext.current

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
    var datePostedIsFocused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

        // Get the current date
        val datePosted = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

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

        val authViewModel = AuthViewModel()
        val authState by authViewModel.authState.observeAsState(AuthState.Loading)

        Button(
            onClick = {
                if (jobTitle.isNotEmpty() && company.isNotEmpty() && location.isNotEmpty() && description.isNotEmpty()) {
                    uploadJobToFirestore(jobTitle, company, location, description, datePosted, context, db)
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = authState !is AuthState.Loading,
            colors = buttonColors,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (authState is AuthState.Loading) {
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

private fun uploadJobToFirestore(jobTitle: String, company: String, location: String, description: String, datePosted: String, context: Context, db: FirebaseFirestore) {
    // Create job data
    val jobData = hashMapOf(
        "title" to jobTitle,
        "company" to company,
        "location" to location,
        "description" to description,
        "datePosted" to datePosted
    )

    // Save job data to Firestore
    db.collection("jobs")
        .add(jobData)
        .addOnSuccessListener {
            Toast.makeText(context, "Job posted successfully", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error posting job: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
