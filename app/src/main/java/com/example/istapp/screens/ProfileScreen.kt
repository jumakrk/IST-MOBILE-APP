package com.example.istapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.istapp.AuthViewModel
import com.example.istapp.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val userProfile by profileViewModel.userProfile.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var isUpdatingUsername by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
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
                    onNavigate = { scope.launch { drawerState.close() } }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Profile Picture Section with more spacing
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.Red),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.username.firstOrNull()?.uppercase() ?: "U",
                                color = Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Spacer to push the card down
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // User Information Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Account Information",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )

                            // Username Section
                            ProfileInfoRow(
                                icon = Icons.Default.Person,
                                label = "Username",
                                value = userProfile.username,
                                onChangeClick = { showUsernameDialog = true }
                            )

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                            // Email Section
                            ProfileInfoRow(
                                icon = Icons.Default.Email,
                                label = "Email",
                                value = userProfile.email,
                                onChangeClick = null
                            )

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                            // Password Section
                            ProfileInfoRow(
                                icon = Icons.Default.Lock,
                                label = "Password",
                                value = "••••••••",
                                onChangeClick = { showResetDialog = true }
                            )
                        }
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Show dialogs...
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Password") },
            text = { Text("We will send a password reset link to your email address: ${userProfile.email}") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.resetPassword(userProfile.email)
                        showResetDialog = false
                        Toast.makeText(context, "Password reset link sent to your email", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Text("Send Link", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Username change dialog
    if (showUsernameDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!isUpdatingUsername) {
                    showUsernameDialog = false
                    newUsername = ""
                }
            },
            title = { Text("Change Username") },
            text = {
                Column {
                    if (isUpdatingUsername) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Red
                        )
                    } else {
                        OutlinedTextField(
                            value = newUsername,
                            onValueChange = { newUsername = it },
                            label = { Text("New Username") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (newUsername.isNotBlank()) {
                                        isUpdatingUsername = true
                                        scope.launch {
                                            try {
                                                profileViewModel.updateUsername(newUsername)
                                                    .onSuccess {
                                                        Toast.makeText(context, "Username updated successfully", Toast.LENGTH_SHORT).show()
                                                        showUsernameDialog = false
                                                        newUsername = ""
                                                    }
                                                    .onFailure {
                                                        Toast.makeText(context, "Failed to update username: ${it.message}", Toast.LENGTH_LONG).show()
                                                    }
                                            } finally {
                                                isUpdatingUsername = false
                                            }
                                        }
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Red,
                                focusedLabelColor = Color.Red,
                                cursorColor = Color.Red
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newUsername.isNotBlank()) {
                            isUpdatingUsername = true
                            scope.launch {
                                try {
                                    profileViewModel.updateUsername(newUsername)
                                        .onSuccess {
                                            Toast.makeText(context, "Username updated successfully", Toast.LENGTH_SHORT).show()
                                            showUsernameDialog = false
                                            newUsername = ""
                                        }
                                        .onFailure {
                                            Toast.makeText(context, "Failed to update username: ${it.message}", Toast.LENGTH_LONG).show()
                                        }
                                } finally {
                                    isUpdatingUsername = false
                                }
                            }
                        }
                    },
                    enabled = !isUpdatingUsername && newUsername.isNotBlank()
                ) {
                    Text("Update", color = if (!isUpdatingUsername) Color.Red else Color.Gray)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showUsernameDialog = false
                        newUsername = ""
                    },
                    enabled = !isUpdatingUsername
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    onChangeClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        if (onChangeClick != null) {
            TextButton(onClick = onChangeClick) {
                Text(
                    text = "Change",
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
} 