package com.example.workhive

import androidx.lifecycle.ViewModel
import com.example.workhive.model.Role
import com.example.workhive.model.Task
import com.example.workhive.model.TeamMember
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class MainViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ───────────────── USER ─────────────────
    private val _currentUser = MutableStateFlow<TeamMember?>(null)
    val currentUser = _currentUser.asStateFlow()

    // ───────────────── MEMBERS (EMPLOYEES) ─────────────────
    private val _members = MutableStateFlow<List<TeamMember>>(emptyList())
    val members = _members.asStateFlow()

    // ───────────────── TASKS ─────────────────
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()

    // ───────────────── LOADING ─────────────────
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // =========================================================
    // 🔥 Load logged-in user & start listeners
    // =========================================================
    fun loadUser(uid: String) {
        db.collection("users")
            .document(uid)
            .addSnapshotListener { snap, _ ->
                if (snap != null && snap.exists()) {
                    val user = snap.toObject(TeamMember::class.java)
                    _currentUser.value = user

                    user?.let {
                        val role = Role.fromString(it.role)
                        loadTasks(role, it.uid)

                        if (role == Role.MANAGER) {
                            loadEmployees()
                        }
                    }
                }
            }
    }

    // =========================================================
    // 🔥 Load tasks + auto-expire logic
    // =========================================================
    private fun loadTasks(role: Role, uid: String) {

        val query = when (role) {
            Role.MANAGER ->
                db.collection("tasks").whereEqualTo("assignedBy", uid)

            Role.EMPLOYEE ->
                db.collection("tasks").whereEqualTo("assignedTo", uid)
        }

        query.addSnapshotListener { snap, _ ->
            val now = Date()

            val list = snap?.documents?.mapNotNull { doc ->
                val task = doc.toObject(Task::class.java)
                    ?.copy(id = doc.id)
                    ?: return@mapNotNull null

                val deadline = task.deadline?.toDate()
                val isExpired =
                    deadline != null &&
                            task.status != "Completed" &&
                            deadline.before(now)

                // 🔥 Auto-expire task
                if (isExpired && task.status != "Expired") {
                    db.collection("tasks")
                        .document(doc.id)
                        .update("status", "Expired")

                    task.copy(status = "Expired")
                } else {
                    task
                }
            } ?: emptyList()

            _tasks.value = list
        }
    }

    // =========================================================
    // 🔥 Load all employees (Manager only)
    // =========================================================
    private fun loadEmployees() {
        db.collection("users")
            .whereEqualTo("role", Role.EMPLOYEE.name)
            .addSnapshotListener { snap, _ ->
                _members.value = snap?.documents?.mapNotNull {
                    it.toObject(TeamMember::class.java)
                } ?: emptyList()
            }
    }

    // =========================================================
    // 🔥 Assign task (with optional deadline)
    // =========================================================
    fun assignTask(
        title: String,
        description: String,
        employeeId: String,
        deadline: Date?
    ) {
        val managerUid = auth.currentUser?.uid ?: return

        if (title.isBlank() || description.isBlank()) return

        val task = Task(
            title = title,
            description = description,
            assignedTo = employeeId,
            assignedBy = managerUid,
            assignedByRole = Role.MANAGER.name,
            status = "Pending",
            deadline = deadline?.let { Timestamp(it) },
            timestamp = Timestamp.now()
        )

        db.collection("tasks").add(task)
    }

    // =========================================================
    // 🔥 Mark task as completed
    // =========================================================
    fun markTaskComplete(taskId: String) {
        db.collection("tasks")
            .document(taskId)
            .update("status", "Completed")
    }

    // =========================================================
    // 🔥 Delete task (Manager only)
    // =========================================================
    fun deleteTask(taskId: String) {
        db.collection("tasks")
            .document(taskId)
            .delete()
    }
}
