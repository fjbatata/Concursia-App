package com.concursia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.concursia.ConcursiaApp
import com.concursia.ui.paywall.PaywallScreen
import com.concursia.ui.splash.SplashScreen
import com.concursia.ui.home.HomeScreen
import com.concursia.ui.concurso.ConcursoDetailScreen
import com.concursia.ui.study.StudyScreen
import com.concursia.ui.quiz.QuizScreen
import com.concursia.ui.quiz.QuizResultScreen
import com.concursia.ui.progress.ProgressScreen
import com.concursia.ui.subject.SubjectDetailScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val app = LocalContext.current.applicationContext as ConcursiaApp
    val repository = app.repository
    val subscriptionManager = app.subscriptionManager

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                subscriptionManager = subscriptionManager,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToPaywall = {
                    navController.navigate(Screen.Paywall.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Paywall.route) {
            PaywallScreen(
                subscriptionManager = subscriptionManager,
                onPurchaseSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Paywall.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                repository = repository,
                onConcursoClick = { concursoId ->
                    navController.navigate(Screen.ConcursoDetail.createRoute(concursoId))
                },
                onQuizClick = { concursoId ->
                    navController.navigate(Screen.Quiz.createRoute(concursoId))
                },
                onProgressClick = {
                    navController.navigate(Screen.Progress.route)
                }
            )
        }

        composable(
            route = Screen.ConcursoDetail.route,
            arguments = listOf(navArgument("concursoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val concursoId = backStackEntry.arguments?.getString("concursoId") ?: return@composable
            ConcursoDetailScreen(
                repository = repository,
                concursoId = concursoId,
                onBack = { navController.popBackStack() },
                onSubjectClick = { subjectId ->
                    navController.navigate(Screen.SubjectDetail.createRoute(subjectId, concursoId))
                },
                onQuizClick = { subjectId ->
                    navController.navigate(Screen.Quiz.createRoute(concursoId, subjectId))
                }
            )
        }

        composable(
            route = Screen.SubjectDetail.route,
            arguments = listOf(
                navArgument("subjectId") { type = NavType.StringType },
                navArgument("concursoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: return@composable
            val concursoId = backStackEntry.arguments?.getString("concursoId") ?: return@composable
            SubjectDetailScreen(
                repository = repository,
                subjectId = subjectId,
                concursoId = concursoId,
                onBack = { navController.popBackStack() },
                onTopicClick = { topicId ->
                    navController.navigate(Screen.Study.createRoute(topicId))
                }
            )
        }

        composable(
            route = Screen.Study.route,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
            StudyScreen(
                repository = repository,
                topicId = topicId,
                onBack = { navController.popBackStack() },
                onComplete = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("concursoId") { type = NavType.StringType },
                navArgument("subjectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val concursoId = backStackEntry.arguments?.getString("concursoId") ?: return@composable
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: "all"
            QuizScreen(
                repository = repository,
                concursoId = concursoId,
                subjectId = subjectId,
                onBack = { navController.popBackStack() },
                onResult = { attemptId ->
                    navController.navigate(Screen.QuizResult.createRoute(attemptId)) {
                        popUpTo(Screen.Quiz.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.QuizResult.route,
            arguments = listOf(navArgument("attemptId") { type = NavType.StringType })
        ) { backStackEntry ->
            val attemptId = backStackEntry.arguments?.getString("attemptId") ?: return@composable
            QuizResultScreen(
                repository = repository,
                attemptId = attemptId,
                onBack = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                repository = repository,
                onBack = { navController.popBackStack() },
                onConcursoClick = { concursoId ->
                    navController.navigate(Screen.ConcursoDetail.createRoute(concursoId))
                }
            )
        }
    }
}