package com.example.istapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.istapp.AuthViewModel
import com.example.istapp.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewUsersScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userType: String
) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Fetch users
    LaunchedEffect(userType) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                users = documents.mapNotNull { it.toObject(User::class.java) }
                    .sortedBy { it.username.lowercase() }
            }
    }

    // Filter users based on search query and userType
    val filteredUsers = users.filter { user ->
        (user.role == userType) && 
        (user.username.contains(searchQuery, ignoreCase = true) ||
         user.email.contains(searchQuery, ignoreCase = true))
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
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
            Column(
                    modifier = Modifier
                        .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Updated Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    placeholder = { Text("Search by name or email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Red,
                        unfocusedBorderColor = Color.Gray,
                        focusedLeadingIconColor = Color.Red,
                        unfocusedLeadingIconColor = Color.Gray,
                        cursorColor = Color.Red // Add red cursor
                    ),
                    shape = RoundedCornerShape(12.dp), // Add rounded corners
                    singleLine = true
                )

                // Updated Header Row (removed Role column)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Username",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold
                    )
                        Text(
                        text = "Email",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(2f), // Increased weight for email
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Updated Users List (removed Role column)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredUsers) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = user.username,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(2f) // Increased weight for email
                            )
                            IconButton(
                                onClick = {
                                selectedUser = user
                                    showDialog = true
                                },
                                modifier = Modifier.weight(0.5f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Change Role",
                                    tint = Color.Red
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }

            // Confirmation Dialog
            if (showDialog && selectedUser != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                                Text(
                            text = "Change User Role",
                            style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                    },
                    text = {
                        Text(
                            text = "Are you sure you want to change ${selectedUser?.username}'s role from ${selectedUser?.role} to ${if (selectedUser?.role == "user") "admin" else "user"}?",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedUser?.let { user ->
                                    val newRole = if (user.role == "user") "admin" else "user"
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("users").document(user.uid)
                                        .update("role", newRole)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Role updated successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // Refresh the users list
                                            db.collection("users")
                                                .get()
                                                .addOnSuccessListener { documents ->
                                                    users = documents.mapNotNull { 
                                                        it.toObject(User::class.java) 
                                                    }.sortedBy { it.username.lowercase() }
                                                }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Failed to update role",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                                showDialog = false
                            }
                        ) {
                            Text(
                                text = "Yes",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(
                                text = "No",
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                )
            }
        }
    }
} 