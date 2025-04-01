package com.mindcare.mindcare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mindcare.mindcare.data.converters.Converters
import com.mindcare.mindcare.data.dao.JournalEntryDao
import com.mindcare.mindcare.data.dao.MeditationSessionDao
import com.mindcare.mindcare.data.dao.UserDao
import com.mindcare.mindcare.data.entities.JournalEntryEntity
import com.mindcare.mindcare.data.entities.MeditationSessionEntity
import com.mindcare.mindcare.data.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        MeditationSessionEntity::class,
        JournalEntryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun meditationSessionDao(): MeditationSessionDao
    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        private const val DATABASE_NAME = "mindcare_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Inicialización de datos si es necesario
                    }
                })
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ejemplo de migración
                database.execSQL("""
                    ALTER TABLE meditation_sessions 
                    ADD COLUMN mood TEXT
                """)
            }
        }
    }
} 