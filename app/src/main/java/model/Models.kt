package com.example.workhive.model

import com.google.firebase.Timestamp

enum class Role(val displayName: String) {
    MANAGER("Manager"),
    EMPLOYEE("Employee");

    companion object {
        fun fromString(role: String): Role {
            return when (role.trim().uppercase()) {
                "MANAGER" -> MANAGER
                else -> EMPLOYEE
            }
        }
    }
}

data class TeamMember(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "EMPLOYEE"
)



data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val assignedTo: String = "",
    val assignedBy: String = "",
    val assignedByRole: String = "",
    val status: String = "Pending",

    // NEW FIELDS ↓↓↓
    val deadline: Timestamp? = null,          // Firestore timestamp
    val priority: String? = null,             // "high", "medium", "low"

    val timestamp: Timestamp? = null          // Created at
)

// Backend request/response simple models
data class TaskRequest(val title: String)
data class TaskAIResponse(val description: String?)
data class SummaryResponse(val summary: String?)
data class RecommendResponse(val recommendedEmployee: String?, val reason: String?)
