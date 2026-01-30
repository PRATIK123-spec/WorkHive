package navigation

import androidx.navigation.NavHostController
import com.example.workhive.model.Role

/**
 * Navigation helper for moving from Auth → Tasks
 */
fun navigateToTaskScreen(
    navController: NavHostController,
    role: Role
) {
    navController.navigate("tasks") {
        popUpTo("auth") { inclusive = true }
        launchSingleTop = true
    }
}
