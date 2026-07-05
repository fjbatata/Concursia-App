package com.concursia.ui.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.concursia.data.database.entity.QuizAttemptEntity
import com.concursia.data.database.entity.StudySessionEntity
import com.concursia.data.repository.ConcursiaRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    repository: ConcursiaRepository,
    onBack: () -> Unit,
    onConcursoClick: (String) -> Unit
) {
    val totalStudyTime by repository.getTotalStudyTime().collectAsState(initial = null)
    val recentSessions by repository.getRecentSessions().collectAsState(initial = emptyList())
    val quizAttempts by repository.getAllQuizAttempts().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Meu Progresso") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stats cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "⏱️",
                        title = "Tempo Total",
                        value = formatTime(totalStudyTime ?: 0)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "📝",
                        title = "Simulados",
                        value = "${quizAttempts.size}"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val avgScore = quizAttempts.map { it.score }.average().let {
                        if (it.isNaN()) null else (it * 100).toInt()
                    }
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "🎯",
                        title = "Média de Acertos",
                        value = if (avgScore != null) "${avgScore}%" else "---"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = "🔥",
                        title = "Dias Estudados",
                        value = "${recentSessions.groupBy { it.completedAt / 86400000 }.size}"
                    )
                }
            }

            // Últimos simulados
            if (quizAttempts.isNotEmpty()) {
                item {
                    Text(
                        "📋 Últimos Simulados",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(quizAttempts.take(10)) { attempt ->
                    QuizAttemptCard(attempt = attempt)
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📝", fontSize = 40.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Nenhum simulado realizado ainda",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Vá para Início e faça um simulado! 🚀",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Sessões recentes de estudo
            if (recentSessions.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "📖 Últimas Sessões de Estudo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(recentSessions.take(10)) { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Book, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Sessão de estudo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${session.timeSpentMinutes} minutos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.dp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuizAttemptCard(attempt: QuizAttemptEntity) {
    val scorePercent = (attempt.score * 100).toInt()
    val scoreColor = when {
        scorePercent >= 70 -> MaterialTheme.colorScheme.tertiary
        scorePercent >= 50 -> MaterialTheme.colorScheme.warning
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score circular
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = scoreColor.copy(alpha = 0.15f),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "${scorePercent}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${attempt.correctAnswers}/${attempt.totalQuestions} acertos",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "${attempt.timeSpentSeconds / 60}min de duração",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Ícone de status
            Icon(
                if (scorePercent >= 60) Icons.Default.ThumbUp else Icons.Default.TrendingUp,
                null,
                tint = scoreColor
            )
        }
    }
}

// Needed for warning color in StatCard
private val MaterialTheme.warning: androidx.compose.ui.graphics.Color
    get() = androidx.compose.ui.graphics.Color(0xFFF57C00)

private fun formatTime(minutes: Int): String {
    return when {
        minutes < 60 -> "${minutes}min"
        minutes < 1440 -> "${minutes / 60}h ${minutes % 60}min"
        else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
    }
}