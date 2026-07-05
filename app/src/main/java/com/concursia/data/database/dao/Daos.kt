package com.concursia.data.database.dao

import androidx.room.*
import com.concursia.data.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConcursoDao {
    @Query("SELECT * FROM concursos ORDER BY createdAt DESC")
    fun getAllConcursos(): Flow<List<ConcursoEntity>>

    @Query("SELECT * FROM concursos WHERE status = 'Vigente' ORDER BY examDate ASC")
    fun getActiveConcursos(): Flow<List<ConcursoEntity>>

    @Query("SELECT * FROM concursos WHERE isFavorite = 1")
    fun getFavoriteConcursos(): Flow<List<ConcursoEntity>>

    @Query("SELECT * FROM concursos WHERE id = :id")
    suspend fun getConcursoById(id: String): ConcursoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcursos(concursos: List<ConcursoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcurso(concurso: ConcursoEntity)

    @Query("UPDATE concursos SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)

    @Query("DELETE FROM concursos")
    suspend fun deleteAll()
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects WHERE concursoId = :concursoId ORDER BY `order` ASC")
    fun getSubjectsByConcurso(concursoId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Query("UPDATE subjects SET completedTopics = :completed WHERE id = :id")
    suspend fun updateCompletedTopics(id: String, completed: Int)
}

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE subjectId = :subjectId ORDER BY `order` ASC")
    fun getTopicsBySubject(subjectId: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: String): TopicEntity?

    @Query("SELECT * FROM topics WHERE concursoId = :concursoId AND isCompleted = 0 ORDER BY `order` ASC LIMIT 10")
    suspend fun getNextTopics(concursoId: String): List<TopicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicEntity>)

    @Query("UPDATE topics SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: String)

    @Query("SELECT COUNT(*) FROM topics WHERE subjectId = :subjectId AND isCompleted = 1")
    fun getCompletedTopicsCount(subjectId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM topics WHERE subjectId = :subjectId")
    fun getTotalTopicsCount(subjectId: String): Flow<Int>
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE topicId = :topicId")
    suspend fun getQuestionsByTopic(topicId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE concursoId = :concursoId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestions(concursoId: String, limit: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE concursoId = :concursoId AND subjectId IN (:subjectIds) ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsBySubjects(concursoId: String, subjectIds: List<String>, limit: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: String): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)
}

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY completedAt DESC")
    fun getAllAttempts(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE concursoId = :concursoId ORDER BY completedAt DESC LIMIT 20")
    fun getAttemptsByConcurso(concursoId: String): Flow<List<QuizAttemptEntity>>

    @Query("SELECT AVG(score) FROM quiz_attempts WHERE concursoId = :concursoId")
    fun getAverageScore(concursoId: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity)

    @Query("DELETE FROM quiz_attempts")
    suspend fun deleteAll()
}

@Dao
interface StudySessionDao {
    @Query("SELECT SUM(timeSpentMinutes) FROM study_sessions")
    fun getTotalStudyTime(): Flow<Int?>

    @Query("SELECT SUM(timeSpentMinutes) FROM study_sessions WHERE concursoId = :concursoId")
    fun getStudyTimeByConcurso(concursoId: String): Flow<Int?>

    @Query("SELECT * FROM study_sessions ORDER BY completedAt DESC LIMIT 30")
    fun getRecentSessions(): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity)

    @Query("SELECT COUNT(DISTINCT date(completedAt / 1000, 'unixepoch')) FROM study_sessions WHERE completedAt >= :since")
    fun getStudyDays(since: Long): Flow<Int>
}