@Entity(tableName = "user_metrics")
data class UserMetricsEntity(
    @PrimaryKey
    val id: Long = 0,
    val userId: String,
    val timestamp: Long,
    val meditationMinutes: Int,
    val moodAverage: Float,
    val stressLevel: Int,
    val sleepQuality: Int,
    val focusScore: Int,
    val mindfulnessScore: Int,
    val consistencyStreak: Int,
    @Embedded
    val healthMetrics: HealthMetrics
)

data class HealthMetrics(
    val heartRateAvg: Float?,
    val respirationRate: Float?,
    val bloodPressure: BloodPressure?,
    val sleepHours: Float?
) 