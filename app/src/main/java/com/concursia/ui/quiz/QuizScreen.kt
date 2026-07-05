package com.concursia.ui.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concursia.data.database.entity.QuestionEntity
import com.concursia.data.database.entity.QuizAttemptEntity
import com.concursia.data.repository.ConcursiaRepository
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    repository: ConcursiaRepository,
    concursoId: String,
    subjectId: String,
    onBack: () -> Unit,
    onResult: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var showResults by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(concursoId, subjectId) {
        isLoading = true
        questions = if (subjectId == "all") {
            repository.getRandomQuestions(concursoId, 10)
        } else {
            repository.getRandomQuestionsBySubjects(concursoId, listOf(subjectId), 10)
        }
        isLoading = false
        startTime = System.currentTimeMillis()
    }

    // Tela de resultado
    if (showResults) {
        val correctCount = questions.filterIndexed { index, q ->
            selectedAnswers[index] == q.correctIndex
        }.size
        val score = if (questions.isNotEmpty()) correctCount.toDouble() / questions.size else 0.0
        val timeSpent = (System.currentTimeMillis() - startTime) / 1000

        // Salva o resultado
        LaunchedEffect(Unit) {
            val attemptId = UUID.randomUUID().toString()
            repository.saveQuizAttempt(
                QuizAttemptEntity(
                    id = attemptId,
                    concursoId = concursoId,
                    subjectId = if (subjectId == "all") null else subjectId,
                    totalQuestions = questions.size,
                    correctAnswers = correctCount,
                    wrongAnswers = questions.size - correctCount,
                    score = score,
                    timeSpentSeconds = timeSpent,
                    questions = questions.map { it.id }
                )
            )
            onResult(attemptId)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📝 Simulado - ${questions.size} questões") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            if (questions.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Anterior
                        if (currentIndex > 0) {
                            OutlinedButton(
                                onClick = { currentIndex-- },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Anterior")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        // Próxima / Finalizar
                        if (currentIndex < questions.size - 1) {
                            Button(
                                onClick = { currentIndex++ },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Próxima")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowForward, null)
                            }
                        } else {
                            Button(
                                onClick = { showResults = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Icon(Icons.Default.CheckCircle, null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Finalizar")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Preparando questões...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("😕", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Nenhuma questão disponível", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            val question = questions[currentIndex]
            val selected = selectedAnswers[currentIndex]

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Progresso
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Barra de progresso
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "${currentIndex + 1}/${questions.size}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / questions.size },
                            modifier = Modifier.weight(1f).height(6.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Enunciado
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            question.question,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Opções
                itemsIndexed(question.options) { index, option ->
                    val isSelected = selected == index
                    val isCorrect = question.correctIndex == index

                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        onClick = {
                            selectedAnswers = selectedAnswers + (currentIndex to index)
                        },
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected) 2.dp else 0.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Indicador de seleção
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedAnswers = selectedAnswers + (currentIndex to index)
                                }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                option,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Placeholder
                if (selected != null) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "✅ Respondida - Vá para a próxima",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    repository: ConcursiaRepository,
    attemptId: String,
    onBack: () -> Unit
) {
    // Como o attempt foi salvo no quiz, mostramos uma tela de parabéns
    // e redirecionamos para home

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Home, "Início")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🎉", fontSize = 80.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Simulado Concluído!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Continue praticando para garantir sua vaga! 🚀",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Voltar ao Início", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}