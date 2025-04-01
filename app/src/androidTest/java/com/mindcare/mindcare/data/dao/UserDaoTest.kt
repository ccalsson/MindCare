@HiltAndroidTest
class UserDaoTest : BaseDatabaseTest() {
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        userDao = database.userDao()
    }

    @Test
    fun insertAndRetrieveUser() = runTest {
        // Given
        val user = UserEntity(
            id = "1",
            email = "test@example.com",
            name = "Test User",
            profilePicture = null,
            preferences = "{}",
            createdAt = System.currentTimeMillis(),
            lastLogin = System.currentTimeMillis(),
            status = UserStatus.ACTIVE,
            securityLevel = 1
        )

        // When
        userDao.insertUser(user)
        val retrieved = userDao.getUser(user.id)

        // Then
        assertThat(retrieved).isEqualTo(user)
    }

    @Test
    fun updateUserStatus() = runTest {
        // Given
        val user = UserEntity(
            id = "1",
            email = "test@example.com",
            name = "Test User",
            profilePicture = null,
            preferences = "{}",
            createdAt = System.currentTimeMillis(),
            lastLogin = System.currentTimeMillis(),
            status = UserStatus.ACTIVE,
            securityLevel = 1
        )
        userDao.insertUser(user)

        // When
        userDao.updateStatus(user.id, UserStatus.BLOCKED)
        val updated = userDao.getUser(user.id)

        // Then
        assertThat(updated?.status).isEqualTo(UserStatus.BLOCKED)
    }

    @Test
    fun getUserWithSessions() = runTest {
        // Given
        val user = UserEntity(/* ... */)
        val session = MeditationSessionEntity(
            userId = user.id,
            meditationType = "mindfulness",
            duration = 600,
            completionRate = 1.0f,
            timestamp = System.currentTimeMillis()
        )

        userDao.insertUser(user)
        database.meditationSessionDao().insertSession(session)

        // When
        val userWithSessions = userDao.getUserWithSessions(user.id)

        // Then
        assertThat(userWithSessions.user).isEqualTo(user)
        assertThat(userWithSessions.sessions).hasSize(1)
        assertThat(userWithSessions.sessions[0]).isEqualTo(session)
    }
} 