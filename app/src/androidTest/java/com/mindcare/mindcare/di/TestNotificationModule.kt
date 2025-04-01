package com.mindcare.mindcare.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.mindcare.mindcare.data.AppDatabase
import com.mindcare.mindcare.data.preferences.NotificationPreferencesManager
import com.mindcare.mindcare.notifications.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestNotificationModule {

    @Singleton
    @Provides
    fun provideTestDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    fun provideTestNotificationService(
        @ApplicationContext context: Context,
        workManager: WorkManager,
        preferencesManager: NotificationPreferencesManager
    ): NotificationService {
        return NotificationService(context, workManager, preferencesManager)
    }

    @Singleton
    @Provides
    fun provideTestWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
} 