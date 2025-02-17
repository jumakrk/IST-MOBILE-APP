package com.example.istapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.istapp.screens.Job
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class JobViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // Fetch job details
    fun getJobDetails(jobId: String): Flow<Job?> = flow {
        val document = firestore.collection("jobs").document(jobId).get().await()
        emit(document.toObject(Job::class.java)?.copy(id = document.id))
    }.flowOn(Dispatchers.IO)

    // Delete job
    suspend fun deleteJob(jobId: String) {
        try {
            firestore.collection("jobs").document(jobId).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete job: ${e.message}")
        }
    }
}
