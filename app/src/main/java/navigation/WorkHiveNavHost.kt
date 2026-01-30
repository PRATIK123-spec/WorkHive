package com.example.workhive.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.workhive.AuthViewModel
import com.example.workhive.MainViewModel
import com.example.workhive.screens.AuthScreen
import com.example.workhive.screens.SplashScreen
import com.example.workhive.screens.TaskScreen

@Composable
fun WorkHiveNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen { navController.navigate("auth") { popUpTo("splash") { inclusive = true } } }
        }

        composable("auth") {
            AuthScreen(navController = navController, authViewModel = authViewModel, mainViewModel = mainViewModel)
        }

        // after successful login the auth screen triggers mainViewModel.loadUser(uid) and navigates to tasks
        composable("tasks") {
            TaskScreen(mainViewModel)
        }
    }
}
