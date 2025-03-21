package com.example.istapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.istapp.screens.Job
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class JobViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _refreshTrigger = MutableSharedFlow<Unit>()
    val refreshTrigger = _refreshTrigger.asSharedFlow()

    // Fetch job details
    fun getJobDetails(jobId: String): Flow<Job?> = flow {
        val document = firestore.collection("jobs").document(jobId).get().await()
        emit(document.toObject(Job::class.java)?.copy(id = document.id))
    }.flowOn(Dispatchers.IO)

    // Delete job with refresh trigger
    suspend fun deleteJob(jobId: String) {
        try {
            firestore.collection("jobs").document(jobId).delete().await()
            _refreshTrigger.emit(Unit) // Trigger refresh after successful deletion
        } catch (e: Exception) {
            throw Exception("Failed to delete job: ${e.message}")
        }
    }

    // Update job with refresh trigger
    suspend fun updateJob(
        jobId: String,
        title: String,
        company: String,
        location: String,
        description: String,
        applicationDeadline: String
    ) {
        try {
            val updates = hashMapOf(
                "title" to title,
                "company" to company,
                "location" to location,
                "description" to description,
                "applicationDeadline" to applicationDeadline
            )
            
            firestore.collection("jobs")
                .document(jobId)
                .update(updates as Map<String, Any>)
                .await()
            _refreshTrigger.emit(Unit) // Trigger refresh after successful update
        } catch (e: Exception) {
            throw Exception("Failed to update job: ${e.message}")
        }
    }
} 