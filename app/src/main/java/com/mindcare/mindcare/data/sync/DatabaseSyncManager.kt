@Singleton
class DatabaseSyncManager @Inject constructor(
    private val database: AppDatabase,
    private val apiService: ApiService,
    private val encryptionManager: DatabaseEncryption,
    private val workManager: WorkManager
) {
    fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWork = PeriodicWorkRequestBuilder<DatabaseSyncWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "database_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWork
        )
    }

    suspend fun performSync() {
        val lastSyncTimestamp = getLastSyncTimestamp()
        val changes = getLocalChanges(lastSyncTimestamp)
        
        try {
            val serverChanges = apiService.syncData(changes)
            applyServerChanges(serverChanges)
            updateLastSyncTimestamp()
        } catch (e: Exception) {
            logSyncError(e)
            scheduleRetry()
        }
    }
} 