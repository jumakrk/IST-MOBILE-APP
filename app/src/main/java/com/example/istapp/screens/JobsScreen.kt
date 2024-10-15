package com.example.istapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.istapp.AuthViewModel
import com.example.istapp.nav.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JobsScreen(navController: NavHostController, authViewModel: AuthViewModel) {
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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.postJob) }, // Navigate to the add job screen
                    shape = RoundedCornerShape(40),
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Job"
                    )
                }
            },
            content = { paddingValues ->
                JobsScreenContent(
                    paddingValues = paddingValues,
                    navController = navController
                )
            },
        )
    }
}

data class Job(
    val title: String,
    val company: String,
    val location: String,
    val description: String
)

@Composable
fun JobsScreenContent(
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    val jobs = remember {
        listOf(
            Job("Software Engineer", "Tech Corp", "New York, NY", "Develop and maintain software applications."),
            Job("Data Analyst", "Data Solutions", "San Francisco, CA", "Analyze and interpret complex data sets."),
            Job("Project Manager", "BuildIt", "Seattle, WA", "Manage projects and coordinate with teams."),
            // Add more job entries here
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(jobs.size) { index ->
            JobCard(job = jobs[index], navController = navController)
        }
    }
}

@Composable
fun JobCard(job: Job, navController: NavHostController) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray)
            .clickable {
                // Navigate to job details
                navController.navigate("${Routes.jobs}/${job.title}")
            }
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = job.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job.company,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job.location,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = job.description,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
