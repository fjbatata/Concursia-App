package com.concursia.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Paywall : Screen("paywall")
    data object Home : Screen("home")
    data object ConcursoDetail : Screen("concurso/{concursoId}") {
        fun createRoute(concursoId: String) = "concurso/$concursoId"
    }
    data object SubjectDetail : Screen("subject/{subjectId}/{concursoId}") {
        fun createRoute(subjectId: String, concursoId: String) = "subject/$subjectId/$concursoId"
    }
    data object Study : Screen("study/{topicId}") {
        fun createRoute(topicId: String) = "study/$topicId"
    }
    data object Quiz : Screen("quiz/{concursoId}/{subjectId}") {
        fun createRoute(concursoId: String, subjectId: String = "all") = "quiz/$concursoId/$subjectId"
    }
    data object QuizResult : Screen("quiz_result/{attemptId}") {
        fun createRoute(attemptId: String) = "quiz_result/$attemptId"
    }
    data object Progress : Screen("progress")
    data object Settings : Screen("settings")
}