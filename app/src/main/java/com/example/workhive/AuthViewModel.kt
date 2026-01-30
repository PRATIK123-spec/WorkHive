package com.example.workhive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workhive.model.TeamMember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _currentUser = MutableStateFlow<TeamMember?>(null)
    val currentUser: StateFlow<TeamMember?> = _currentUser

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener
                    loadUser(uid)
                }
                .addOnFailureListener { e ->
                    _authError.value = e.message
                }
        }
    }

    fun signUp(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener
                    val user = hashMapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email,
                        "role" to role
                    )
                    db.collection("users").document(uid).set(user)
                        .addOnSuccessListener {
                            loadUser(uid)
                        }
                        .addOnFailureListener { e -> _authError.value = e.message }
                }
                .addOnFailureListener { e -> _authError.value = e.message }
        }
    }

    fun loadUser(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val u = doc.toObject(TeamMember::class.java)
                _currentUser.value = u
                _isAuthenticated.value = true
            }
            .addOnFailureListener { e ->
                _authError.value = e.message
            }
    }

    fun logout() {
        auth.signOut()
        _isAuthenticated.value = false
        _currentUser.value = null
    }
}
