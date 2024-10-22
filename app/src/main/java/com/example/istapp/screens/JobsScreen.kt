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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import com.example.istapp.AuthViewModel
import com.example.istapp.nav.Routes
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun JobsScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Observe the user's role from the ViewModel
    val userRole = authViewModel.userRole.observeAsState().value ?: "user"

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
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val description: String = "",
    val postedBy: String = ""
)

object FirestoreService {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    suspend fun getJobs(): List<Job> {
        return try {
            val snapshot = db.collection("jobs").get().await()
            snapshot.documents.mapNotNull { it.toObject(Job::class.java) }
        } catch (e: Exception) {
            emptyList() // Handle errors gracefully
        }
    }
}

class JobsViewModel : ViewModel() {
    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> get() = _jobs

    init {
        // Fetch jobs when the ViewModel is initialized
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
    jobsViewModel: JobsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
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
                JobCard(job = jobs.value[index], navController = navController)
            }
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Posted by: ${job.postedBy}",
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
