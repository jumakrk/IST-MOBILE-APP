package com.example.istapp.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.istapp.AuthViewModel
import com.example.istapp.nav.Routes
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.istapp.viewmodels.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JobsScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val jobsViewModel: JobsViewModel = viewModel()

    // Observe the user's role from the ViewModel
    val userRole = authViewModel.userRole.observeAsState().value ?: "user"

    // Add LaunchedEffect to refresh jobs when screen becomes active
    LaunchedEffect(Unit) {
        jobsViewModel.refreshJobs()
    }

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
            floatingActionButton = {
                if (userRole == "admin") {
                    FloatingActionButton(
                        onClick = { navController.navigate(Routes.postJob) },
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
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val applicationDeadline: String = "",
    val description: String = "",
    val postedBy: String = "",
    val datePosted: String = "",
    val type: String = ""
)

object FirestoreService {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    suspend fun getJobs(): List<Job> {
        return try {
            val snapshot = db.collection("jobs").get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Job::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList() // Handle errors gracefully
        }
    }
}

class JobsViewModel : ViewModel() {
    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> get() = _jobs
    private val jobViewModel = JobViewModel()

    init {
        // Fetch jobs when the ViewModel is initialized
        fetchJobs()
        
        // Listen for refresh triggers
        viewModelScope.launch {
            jobViewModel.refreshTrigger.collect {
                fetchJobs()
            }
        }
    }

    fun refreshJobs() {
        fetchJobs()
    }

    private fun fetchJobs() {
        viewModelScope.launch {
            val jobsList = FirestoreService.getJobs()
            _jobs.value = jobsList
        }
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun JobsScreenContent(
    paddingValues: PaddingValues,
    navController: NavHostController,
    jobsViewModel: JobsViewModel = viewModel()
) {
    val jobs = jobsViewModel.jobs.observeAsState(emptyList())

    if (jobs.value.isEmpty()) {
        // Show a circular loading indicator when there are no jobs
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color.Red,
            )
        }
    } else {
        // Display the list of jobs once they are fetched
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(jobs.value.size) { index ->
                JobCard(job = jobs.value[index], navController)
            }
        }
    }
}


@Composable
fun JobCard(job: Job, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = job.company,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Red,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable { 
                            navController.navigate(Routes.viewJob.replace("{jobId}", job.id))
                        },
                ) {
                    Text(
                        text = "View Details",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = job.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Posted by: ${job.postedBy}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Posted on: ${job.datePosted}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Deadline",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black
                    )
                    Text(
                        text = job.applicationDeadline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
