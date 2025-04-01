@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUser(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET lastLogin = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long)

    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateStatus(userId: String, status: UserStatus)

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithSessions(userId: String): UserWithSessions

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithJournalEntries(userId: String): UserWithJournalEntries
}

data class UserWithSessions(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val sessions: List<MeditationSessionEntity>
)

data class UserWithJournalEntries(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val entries: List<JournalEntryEntity>
) 