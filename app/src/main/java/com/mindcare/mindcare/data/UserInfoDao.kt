import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM user_info WHERE id = :userId")
    suspend fun getUserInfo(userId: Int): UserInfo?

    @Insert
    suspend fun insertUserInfo(userInfo: UserInfo)

    @Update
    suspend fun updateUserInfo(userInfo: UserInfo)
} 