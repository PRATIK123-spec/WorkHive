package com.example.workhive.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.workhive.MainViewModel
import com.example.workhive.model.Role
import com.example.workhive.model.TeamMember

@Composable
fun TeamManagementScreen(
    viewModel: MainViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = null)
    val employees by viewModel.members.collectAsState(initial = emptyList())

    // Only managers can view this — employees cannot reach this screen
    val isManager = currentUser?.role == Role.MANAGER.name

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF0D1117), Color(0xFF161B22))
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(20.dp)
    ) {
        Text(
            text = "👥 Team Members",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        if (!isManager) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "You do not have permission to view this screen.",
                    color = Color(0xFF9AA3B2)
                )
            }
            return
        }

        if (employees.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No employees found.", color = Color(0xFF9AA3B2))
            }
            return
        }

        LazyColumn {
            items(employees) { emp ->
                TeamMemberCard(emp)
            }
        }
    }
}

@Composable
fun TeamMemberCard(member: TeamMember) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242E))
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = member.name.ifBlank { "Unnamed Member" },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Email: ${member.email}",
                color = Color(0xFF9AA3B2)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Role: ${member.role}",
                color = Color(0xFFBFC6D1)
            )
        }
    }
}
