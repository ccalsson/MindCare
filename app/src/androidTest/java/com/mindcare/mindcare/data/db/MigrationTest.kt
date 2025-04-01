@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        // Create version 1 of the database
        helper.createDatabase(TEST_DB, 1).apply {
            // Add some data using SQL
            execSQL("""
                INSERT INTO meditation_sessions (userId, meditationType, duration, timestamp)
                VALUES ('1', 'mindfulness', 600, ${System.currentTimeMillis()})
            """)
            close()
        }

        // Migrate to version 2
        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        // Verify the data
        val cursor = db.query("SELECT * FROM meditation_sessions")
        cursor.moveToFirst()
        assertThat(cursor.getInt(cursor.getColumnIndex("metrics_version"))).isEqualTo(1)
    }
} 