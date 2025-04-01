@HiltAndroidTest
class JournalDaoTest : BaseDatabaseTest() {
    private lateinit var journalDao: JournalDao

    @Before
    fun setup() {
        journalDao = database.journalDao()
    }

    @Test
    fun insertAndRetrieveEntry() = runTest {
        // Given
        val entry = JournalEntryEntity(
            userId = "1",
            content = "Test entry",
            mood = "HAPPY",
            timestamp = System.currentTimeMillis(),
            tags = listOf("test", "mood"),
            location = null,
            weatherConditions = "sunny",
            analysis = null
        )

        // When
        val id = journalDao.insertEntry(entry)
        val retrieved = journalDao.getEntry(id)

        // Then
        assertThat(retrieved?.content).isEqualTo(entry.content)
        assertThat(retrieved?.mood).isEqualTo(entry.mood)
    }

    @Test
    fun getEntriesByDateRange() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val entries = listOf(
            JournalEntryEntity(
                userId = "1",
                content = "Past entry",
                mood = "SAD",
                timestamp = now - 86400000 // Yesterday
            ),
            JournalEntryEntity(
                userId = "1",
                content = "Today's entry",
                mood = "HAPPY",
                timestamp = now
            )
        )

        entries.forEach { journalDao.insertEntry(it) }

        // When
        val todayEntries = journalDao.getEntriesByDateRange(
            userId = "1",
            startTime = now - 43200000, // 12 hours ago
            endTime = now + 43200000    // 12 hours from now
        )

        // Then
        assertThat(todayEntries).hasSize(1)
        assertThat(todayEntries[0].content).isEqualTo("Today's entry")
    }
} 