package com.example.uthtodo

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uthtodo.intro.OnboardingScreen
import com.example.uthtodo.intro.SplashScreen
import com.example.uthtodo.intro.onboardingPages
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Khởi tạo 1 TaskViewModel dùng chung duy nhất cho toàn bộ màn hình
    val taskViewModel: TaskViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onFinished = {
                navController.navigate("onboarding1") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("onboarding1") {
            OnboardingScreen(
                page = onboardingPages[0],
                step = 0,
                onNextClick = { navController.navigate("onboarding2") },
                onSkipClick = { navController.navigate("home") }
            )
        }

        composable("onboarding2") {
            OnboardingScreen(
                page = onboardingPages[1],
                step = 1,
                onNextClick = { navController.navigate("onboarding3") },
                onSkipClick = { navController.navigate("home") }
            )
        }

        composable("onboarding3") {
            OnboardingScreen(
                page = onboardingPages[2],
                step = 2,
                onNextClick = { navController.navigate("loginScreen") },
                onSkipClick = { navController.navigate("loginScreen") }
            )
        }

        composable("loginScreen") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home") {
                    popUpTo("loginScreen") { inclusive = true }
                }
            })
        }

        // BÀI 1: Flow Trang chủ
        composable("home") {
            HomeScreen(
                taskViewModel = taskViewModel,
                onNavigateToTaskList = { navController.navigate("taskList") },
                onNavigateToTaskDetail = { taskId -> navController.navigate("taskDetail/$taskId") },
                onNavigateToAddTask = { navController.navigate("addTask") },
                onNavigateToProfile = { navController.navigate("profileScreen") }
            )
        }

        // BÀI 2: Flow Danh sách
        composable("taskList") {
            TaskListScreen(
                taskViewModel = taskViewModel,
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToTaskDetail = { taskId -> navController.navigate("taskDetail/$taskId") },
                onNavigateToAddTask = { navController.navigate("addTask") },
                onNavigateToProfile = { navController.navigate("profileScreen") }
            )
        }

        // Màn hình Thêm / Sửa công việc mới (theo hình 2)
        composable("addTask") {
            AddTaskScreen(
                taskViewModel = taskViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // BÀI 3: Flow Chi tiết
        composable(
            route = "taskDetail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            TaskDetailScreen(
                taskId = taskId,
                taskViewModel = taskViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Màn hình Hồ sơ cá nhân (BT Về Nhà 01 - UTH Auth)
        composable("profileScreen") {
            ProfileScreen(
                taskViewModel = taskViewModel,
                onLogoutClick = {
                    Firebase.auth.signOut()
                    navController.navigate("loginScreen") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
