import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class UserInfo(
    @PrimaryKey val id: Int,
    val name: String,
    val preferences: String // JSON string for complex data
) 