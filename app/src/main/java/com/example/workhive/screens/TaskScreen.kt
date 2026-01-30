package com.example.workhive.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.workhive.MainViewModel
import com.example.workhive.model.Role
import com.example.workhive.model.Task
import com.example.workhive.model.TeamMember
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: MainViewModel) {

    val currentUser by viewModel.currentUser.collectAsState(initial = null)
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    val employees by viewModel.members.collectAsState(initial = emptyList())

    val role = Role.fromString(currentUser?.role ?: "EMPLOYEE")

    // Manager inputs
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedEmployee by remember { mutableStateOf<TeamMember?>(null) }
    var selectedDeadline by remember { mutableStateOf<Date?>(null) }
    var showEmployeeSheet by remember { mutableStateOf(false) }

    val ctx = LocalContext.current
    val dateFmt = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    fun pickDateTime() {
        val now = Calendar.getInstance()
        DatePickerDialog(ctx, { _, y, m, d ->
            TimePickerDialog(ctx, { _, h, min ->
                Calendar.getInstance().apply {
                    set(y, m, d, h, min, 0)
                    selectedDeadline = time
                }
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false).show()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    val bg = Brush.verticalGradient(
        listOf(Color(0xFF07060A), Color(0xFF111418))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        // HEADER
        Text(
            text = if (role == Role.MANAGER) "Manager Dashboard" else "My Tasks",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        // MANAGER CREATE TASK
        if (role == Role.MANAGER) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F28))
            ) {
                Column(Modifier.padding(16.dp)) {

                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = { showEmployeeSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2F3A))
                    ) {
                        Text(selectedEmployee?.name ?: "Select Employee")
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = { pickDateTime() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2F3A))
                    ) {
                        Text(
                            selectedDeadline?.let { "Deadline: ${dateFmt.format(it)}" }
                                ?: "Set Deadline"
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val emp = selectedEmployee ?: return@Button
                            viewModel.assignTask(title, description, emp.uid, selectedDeadline)
                            title = ""
                            description = ""
                            selectedEmployee = null
                            selectedDeadline = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E57FF))
                    ) {
                        Text("Assign Task")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // TASK LIST
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    role = role,
                    onComplete = { viewModel.markTaskComplete(task.id) },
                    onDelete = { viewModel.deleteTask(task.id) }
                )
            }
        }
    }

    // EMPLOYEE SELECT SHEET
    if (showEmployeeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEmployeeSheet = false },
            containerColor = Color(0xFF111418)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Select Employee", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                employees.forEach { emp ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                selectedEmployee = emp
                                showEmployeeSheet = false
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F28))
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(emp.name, color = Color.White)
                            Text(emp.email, color = Color(0xFF9AA3B2))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    role: Role,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val deadline = task.deadline?.toDate()
    val now = System.currentTimeMillis()
    val timeLeft = max(0, (deadline?.time ?: 0) - now)

    val deadlineText = when {
        deadline == null -> null
        timeLeft <= 0 -> "Expired"
        timeLeft < 3600000 -> "Due < 1 hour"
        timeLeft < 86400000 -> "Due Today"
        else -> "Due in ${timeLeft / 86400000} days"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .shadow(if (expanded) 12.dp else 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242E))
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(task.title, color = Color.White, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(6.dp))

            Text(
                task.description,
                color = Color(0xFFBDC5D1),
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            deadlineText?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, color = if (it == "Expired") Color.Red else Color(0xFF8E57FF))
            }

            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Status: ${task.status}", color = Color(0xFF9AA3B2))

                Row {
                    if (task.status != "Completed") {
                        TextButton(onClick = onComplete) {
                            Text("Done", color = Color(0xFF8E57FF))
                        }
                    }
                    if (role == Role.MANAGER) {
                        TextButton(onClick = onDelete) {
                            Text("Delete", color = Color.Red)
                        }
                    }
                }
            }

            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Show Less" else "Show More")
            }
        }
    }
}
