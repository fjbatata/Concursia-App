package com.concursia.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "concursos")
data class ConcursoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val banca: String,
    val level: String, // Federal, Estadual, Municipal
    val status: String, // Vigente, Previsto, Encerrado
    val vacancies: Int,
    val registrationStart: String?,
    val registrationEnd: String?,
    val examDate: String?,
    val salary: String,
    val imageUrl: String?,
    val subjects: List<String>,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val concursoId: String,
    val title: String,
    val description: String,
    val icon: String,
    val totalTopics: Int = 0,
    val completedTopics: Int = 0,
    val order: Int = 0
)

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val concursoId: String,
    val title: String,
    val content: String, // Markdown content
    val summary: String,
    val duration: Int, // estimated minutes
    val difficulty: String, // Fácil, Médio, Difícil
    val isCompleted: Boolean = false,
    val order: Int = 0
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val topicId: String,
    val concursoId: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val banca: String,
    val year: Int,
    val difficulty: String,
    val subjectId: String = ""
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey val id: String,
    val concursoId: String,
    val subjectId: String?,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val score: Double,
    val timeSpentSeconds: Long,
    val completedAt: Long = System.currentTimeMillis(),
    val questions: List<String> // IDs das questões
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey val id: String,
    val concursoId: String,
    val topicId: String,
    val timeSpentMinutes: Int,
    val completedAt: Long = System.currentTimeMillis()
)