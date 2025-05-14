package com.example.istapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.istapp.viewmodels.AuthViewModel
import com.example.istapp.nav.Routes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                BottomBar(navController = navController)
            },
            content = { paddingValues ->
                HomeScreenContent(
                    paddingValues = paddingValues,
                    navController = navController
                )
            },
        )
    }

    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate(Routes.login) {
                popUpTo(Routes.homepage) { inclusive = true }
            }
        } else if (!currentUser.isEmailVerified) {
            navController.navigate(Routes.verificationEmailSent) {
                popUpTo(Routes.homepage) { inclusive = true }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Welcome Section
        item {
            WelcomeSection()
        }

        // Quick Actions
        item {
            QuickActionsSection(navController)
        }

        // Featured Jobs Section
        item {
            FeaturedJobsSection(navController)
        }

        // Announcements Section
        item {
            AnnouncementsSection()
        }
    }
}

@Composable
fun WelcomeSection() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val username = currentUser?.displayName ?: "User"
    
    // Get current hour for time-based greeting
    val currentHour = java.time.LocalTime.now().hour
    val greeting = when (currentHour) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = greeting,
            fontSize = 18.sp,
            color = Color.Gray
        )
        Text(
            text = username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun QuickActionsSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Quick Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Rounded.Search,
                    label = "Discover Jobs",
                    onClick = { navController.navigate(Routes.jobs) }
                )
                QuickActionButton(
                    icon = Icons.Rounded.Bookmark,
                    label = "Saved Jobs",
                    onClick = { navController.navigate(Routes.jobs) } // TODO: Update with saved jobs route
                )
                QuickActionButton(
                    icon = Icons.Rounded.DoneAll,
                    label = "Applied Jobs",
                    onClick = { navController.navigate(Routes.jobs) } // TODO: Update with applied jobs route
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(90.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Red),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FeaturedJobsSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured Jobs",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = { navController.navigate(Routes.jobs) }
            ) {
                Text(
                    text = "View All",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(sampleJobs) { job ->
                JobCard(job = job, onClick = {
                    navController.navigate("${Routes.viewJob}/${job.id}")
                })
            }
        }
    }
}

@Composable
fun JobCard(
    job: Job,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = job.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = job.company,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Location",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = job.location,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun AnnouncementsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Announcements",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Red.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "New Feature: Job Alerts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Get notified when new jobs matching your preferences are posted!",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// Sample data for demonstration
private val sampleJobs = listOf(
    Job(
        id = "1",
        title = "Senior Software Engineer",
        company = "Tech Solutions Inc.",
        location = "New York, NY",
        type = "Full-time"
    ),
    Job(
        id = "2",
        title = "UX/UI Designer",
        company = "Creative Studios",
        location = "Remote",
        type = "Contract"
    ),
    Job(
        id = "3",
        title = "Product Manager",
        company = "Innovation Labs",
        location = "San Francisco, CA",
        type = "Full-time"
    )
)
