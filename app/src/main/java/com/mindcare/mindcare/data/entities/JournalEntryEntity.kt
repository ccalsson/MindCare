@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("timestamp")]
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val content: String,
    val mood: String,
    val timestamp: Long,
    val tags: List<String>,
    val location: Location?,
    val weatherConditions: String?,
    val analysis: EntryAnalysis?,
    val isEncrypted: Boolean = false
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val placeName: String?
)

data class EntryAnalysis(
    val sentimentScore: Float,
    val topics: List<String>,
    val suggestedActions: List<String>
) 