object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Añadir nuevas columnas con valores por defecto
            database.execSQL("""
                ALTER TABLE meditation_sessions 
                ADD COLUMN metrics_version INTEGER NOT NULL DEFAULT 1
            """)
            
            // Crear tabla temporal
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS meditation_sessions_temp (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    userId TEXT NOT NULL,
                    meditationType TEXT NOT NULL,
                    duration INTEGER NOT NULL,
                    timestamp INTEGER NOT NULL,
                    metrics_version INTEGER NOT NULL,
                    FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE
                )
            """)
            
            // Migrar datos
            database.execSQL("""
                INSERT INTO meditation_sessions_temp 
                SELECT id, userId, meditationType, duration, timestamp, metrics_version 
                FROM meditation_sessions
            """)
            
            // Eliminar tabla antigua y renombrar la nueva
            database.execSQL("DROP TABLE meditation_sessions")
            database.execSQL("ALTER TABLE meditation_sessions_temp RENAME TO meditation_sessions")
            
            // Recrear índices
            database.execSQL("CREATE INDEX IF NOT EXISTS index_meditation_sessions_userId ON meditation_sessions(userId)")
        }
    }
} 