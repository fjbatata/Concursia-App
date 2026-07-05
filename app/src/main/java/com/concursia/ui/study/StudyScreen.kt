package com.concursia.ui.study

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.concursia.data.database.entity.StudySessionEntity
import com.concursia.data.database.entity.TopicEntity
import com.concursia.data.repository.ConcursiaRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    repository: ConcursiaRepository,
    topicId: String,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var topic by remember { mutableStateOf<TopicEntity?>(null) }
    var isCompleted by remember { mutableStateOf(false) }
    var isMarkingComplete by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var showSummary by remember { mutableStateOf(false) }

    LaunchedEffect(topicId) {
        topic = repository.getTopicById(topicId)
        isCompleted = topic?.isCompleted ?: false
        startTime = System.currentTimeMillis()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(topic?.title ?: "Carregando...", style = MaterialTheme.typography.titleMedium)
                        topic?.let {
                            Text(
                                "~${it.duration} min de leitura",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    // Botão de resumo
                    IconButton(onClick = { showSummary = !showSummary }) {
                        Icon(
                            if (showSummary) Icons.Default.Lightbulb else Icons.Default.LightbulbOutline,
                            "Resumo"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (topic != null && !isCompleted) {
                Surface(
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = {
                            isMarkingComplete = true
                            scope.launch {
                                repository.markTopicCompleted(topicId)

                                // Registra sessão de estudo
                                val elapsedMinutes = ((System.currentTimeMillis() - startTime) / 60000).toInt().coerceAtLeast(1)
                                repository.saveStudySession(
                                    StudySessionEntity(
                                        id = "study_${topicId}_${System.currentTimeMillis()}",
                                        concursoId = topic!!.concursoId,
                                        topicId = topicId,
                                        timeSpentMinutes = elapsedMinutes
                                    )
                                )

                                isCompleted = true
                                isMarkingComplete = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isMarkingComplete
                    ) {
                        if (isMarkingComplete) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.CheckCircle, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Concluir Lição", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                topic == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                showSummary -> {
                    // Modo resumo - mostra apenas o summary
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(20.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💡", fontSize = 24.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Resumo do Tópico",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    topic!!.summary,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                TextButton(onClick = { showSummary = false }) {
                                    Text("📖 Voltar ao conteúdo completo")
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Conteúdo completo renderizado como texto
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(20.dp)
                    ) {
                        if (isCompleted) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🎉", fontSize = 28.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Lição Concluída!",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Você dominou este tópico. Bora pro próximo? 🚀",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Renderiza o conteúdo markdown de forma simplificada
                        StudyContent(content = topic!!.content)
                    }
                }
            }
        }
    }
}

@Composable
fun StudyContent(content: String) {
    val lines = content.split("\n")
    var inBlockquote = false

    lines.forEach { line ->
        when {
            line.startsWith("# ") -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    line.removePrefix("# "),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            line.startsWith("## ") -> {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    line.removePrefix("## "),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            line.startsWith("### ") -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    line.removePrefix("### "),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            line.startsWith("> ") -> {
                val text = line.removePrefix("> ")
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            line.startsWith("|") -> {
                // Tabela simples
                val cells = line.split("|").filter { it.isNotBlank() }.map { it.trim() }
                if (cells.isNotEmpty() && !line.contains("---")) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        cells.forEach { cell ->
                            Text(
                                cell,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f).padding(4.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
            line.startsWith("✅") || line.startsWith("- [x]") || line.startsWith("- [X]") -> {
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("✅", fontSize = 14.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        line.replace("✅", "").replace("- [x]", "").replace("- [X]", "").trim(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                Row(modifier = Modifier.padding(vertical = 1.dp)) {
                    Text("  •  ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        line.removePrefix("- ").removePrefix("* "),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            line.startsWith("1.") || line.startsWith("2.") || line.startsWith("3.") ||
            line.startsWith("4.") || line.startsWith("5.") || line.startsWith("6.") ||
            line.startsWith("7.") || line.startsWith("8.") || line.startsWith("9.") || line.startsWith("10.") -> {
                Row(modifier = Modifier.padding(vertical = 1.dp)) {
                    Text(
                        line.substringBefore(".") + ".",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        line.substringAfter(". "),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            line.startsWith("---") -> {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            line.startsWith("**") && line.endsWith("**") -> {
                Text(
                    line.removePrefix("**").removeSuffix("**"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            line.startsWith("*") && line.endsWith("*") && !line.startsWith("* ") -> {
                Text(
                    line.removePrefix("*").removeSuffix("*"),
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            line.isBlank() -> {
                Spacer(modifier = Modifier.height(8.dp))
            }
            else -> {
                Text(
                    line,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}