package com.mindcare.mindcare.di

import android.content.Context
import com.mindcare.mindcare.data.AppDatabase
import com.mindcare.mindcare.data.dao.NotificationDao
import com.mindcare.mindcare.data.dao.ReminderDao
import com.mindcare.mindcare.notifications.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.room.Room

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mindcare_db"
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun provideUserInfoDao(database: AppDatabase): UserInfoDao = database.userInfoDao()

    @Provides
    fun provideJournalDao(database: AppDatabase): JournalDao = database.journalDao()

    @Provides
    fun provideMeditationDao(database: AppDatabase): MeditationDao = database.meditationDao()

    @Provides
    fun provideNotificationDao(database: AppDatabase): NotificationDao = database.notificationDao()

    @Provides
    fun provideReminderDao(database: AppDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Singleton
    @Provides
    fun provideNotificationService(@ApplicationContext context: Context): NotificationService {
        return NotificationService(context)
    }
} 