class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromLocation(location: Location?): String? {
        return location?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toLocation(value: String?): Location? {
        return value?.let {
            gson.fromJson(it, Location::class.java)
        }
    }

    @TypeConverter
    fun fromEntryAnalysis(analysis: EntryAnalysis?): String? {
        return analysis?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toEntryAnalysis(value: String?): EntryAnalysis? {
        return value?.let {
            gson.fromJson(it, EntryAnalysis::class.java)
        }
    }
} 