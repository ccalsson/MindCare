@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val profilePicture: String?,
    val preferences: String, // JSON de preferencias
    val createdAt: Long,
    val lastLogin: Long,
    val status: UserStatus,
    val securityLevel: Int
)

enum class UserStatus {
    ACTIVE, INACTIVE, BLOCKED, PENDING_VERIFICATION
} 