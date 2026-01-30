package com.example.workhive.network

data class RecommendRequest(
    val taskDescription: String,
    val employees: List<RecommendEmployee>
)
