package com.concursia.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.concursia.data.database.dao.*
import com.concursia.data.database.entity.*

@Database(
    entities = [
        ConcursoEntity::class,
        SubjectEntity::class,
        TopicEntity::class,
        QuestionEntity::class,
        QuizAttemptEntity::class,
        StudySessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ConcursiaDatabase : RoomDatabase() {
    abstract fun concursoDao(): ConcursoDao
    abstract fun subjectDao(): SubjectDao
    abstract fun topicDao(): TopicDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizAttemptDao(): QuizAttemptDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        private var INSTANCE: ConcursiaDatabase? = null

        fun getDatabase(context: Context): ConcursiaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConcursiaDatabase::class.java,
                    "concursia_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}