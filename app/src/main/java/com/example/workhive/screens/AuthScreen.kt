package com.example.workhive.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.navigation.NavController
import com.example.workhive.AuthViewModel
import com.example.workhive.MainViewModel
import com.example.workhive.model.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    val gradient = Brush.verticalGradient(listOf(Color(0xFF07060A), Color(0xFF0E0F17)))

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var role by remember { mutableStateOf(Role.EMPLOYEE) }

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val authError by authViewModel.authError.collectAsState()

    // when auth OK, load user into mainViewModel and navigate (keeps your nav logic)
    LaunchedEffect(isAuthenticated, currentUser) {
        if (isAuthenticated && currentUser != null) {
            mainViewModel.loadUser(currentUser!!.uid)
            navController.navigate("tasks") { popUpTo("auth") { inclusive = true } }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .statusBarsPadding() // add top padding to avoid overlap
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1116))
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isLogin) "Welcome back" else "Create your account",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Spacer(Modifier.height(12.dp))

                if (!isLogin) {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full name") },
                        singleLine = true,
                        colors = textFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    colors = textFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                if (!isLogin) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { role = Role.MANAGER },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (role == Role.MANAGER) Color(0xFF8E57FF) else Color.DarkGray
                            )
                        ) { Text("Manager") }

                        Button(
                            onClick = { role = Role.EMPLOYEE },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (role == Role.EMPLOYEE) Color(0xFF8E57FF) else Color.DarkGray
                            )
                        ) { Text("Employee") }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (isLogin) authViewModel.login(email.trim(), password)
                        else authViewModel.signUp(name.trim(), email.trim(), password, role.name)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E57FF))
                ) {
                    Text(if (isLogin) "Login" else "Sign up")
                }

                Spacer(Modifier.height(8.dp))

                TextButton(onClick = { isLogin = !isLogin }) {
                    Text(if (isLogin) "Create account" else "Already have an account? Login", color = Color(0xFF9AA3B2))
                }

                authError?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
