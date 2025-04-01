@Singleton
class DatabaseCache @Inject constructor(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) {
    private val cacheSize = 100
    private val journalCache = LruCache<Long, JournalEntryEntity>(cacheSize)
    private val meditationCache = LruCache<Long, MeditationSessionEntity>(cacheSize)

    init {
        scope.launch {
            preloadFrequentlyAccessedData()
        }
    }

    private suspend fun preloadFrequentlyAccessedData() {
        val recentEntries = database.journalEntryDao()
            .getRecentEntries(limit = cacheSize)
        
        recentEntries.forEach { entry ->
            journalCache.put(entry.id, entry)
        }
    }

    suspend fun getJournalEntry(id: Long): JournalEntryEntity? {
        return journalCache.get(id) ?: database.journalEntryDao()
            .getEntry(id)?.also { 
                journalCache.put(id, it)
            }
    }
} 