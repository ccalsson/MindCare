@Entity(
    tableName = "meditation_sessions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class MeditationSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val meditationType: String,
    val duration: Int,
    val completionRate: Float,
    val timestamp: Long,
    val mood: String?,
    val notes: String?,
    @Embedded
    val metrics: MeditationMetrics
)

data class MeditationMetrics(
    val averageHeartRate: Float?,
    val calmScore: Int?,
    val focusLevel: Int?,
    val interruptions: Int
) 