package com.example.istapp.models

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "user",
    val firstname: String = "",
    val lastname: String = ""
) 