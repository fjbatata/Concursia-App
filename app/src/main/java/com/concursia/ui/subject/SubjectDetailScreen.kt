package com.concursia.ui.subject

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.concursia.data.database.entity.SubjectEntity
import com.concursia.data.database.entity.TopicEntity
import com.concursia.data.repository.ConcursiaRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    repository: ConcursiaRepository,
    subjectId: String,
    concursoId: String,
    onBack: () -> Unit,
    onTopicClick: (String) -> Unit
) {
    var subject by remember { mutableStateOf<SubjectEntity?>(null) }
    val topics by repository.getTopicsBySubject(subjectId).collectAsState(initial = emptyList())
    val completedCount by repository.getCompletedTopicsCount(subjectId).collectAsState(initial = 0)
    val totalCount by repository.getTotalTopicsCount(subjectId).collectAsState(initial = 0)

    LaunchedEffect(subjectId) {
        subject = repository.getSubjectById(subjectId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subject?.title ?: "Carregando...") },
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
            // Progresso da matéria
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "📊 Progresso",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$completedCount/$totalCount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = {
                                if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
                            },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f),
                        )
                    }
                }
            }

            // Lista de tópicos
            item {
                Text(
                    "📖 Tópicos de Estudo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (topics.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nenhum tópico disponível", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(topics) { topic ->
                    TopicCard(
                        topic = topic,
                        onClick = { onTopicClick(topic.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun TopicCard(
    topic: TopicEntity,
    onClick: () -> Unit
) {
    val diffColor = when (topic.difficulty) {
        "Fácil" -> MaterialTheme.colorScheme.tertiary
        "Médio" -> Color(0xFFFFA726L) // warning orange
        "Difícil" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone de conclusão
            Icon(
                if (topic.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (topic.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = diffColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            topic.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            color = diffColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Timer, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        "${topic.duration}min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (topic.isCompleted) {
                Text(
                    "✓ Concluído",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Icon(Icons.Default.ChevronRight, "Abrir", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// Warning color helper needed for MaterialTheme
private val MaterialTheme.warning: androidx.compose.ui.graphics.Color
    get() = androidx.compose.ui.graphics.Color(0xFFF57C00)