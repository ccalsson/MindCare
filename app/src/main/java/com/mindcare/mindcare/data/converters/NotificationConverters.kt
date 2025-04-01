package com.mindcare.mindcare.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindcare.mindcare.notifications.NotificationType
import com.mindcare.mindcare.notifications.ReminderFrequency
import com.mindcare.mindcare.notifications.ReminderType
import java.util.*

class NotificationConverters {
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
    fun fromNotificationType(value: NotificationType): String {
        return value.name
    }

    @TypeConverter
    fun toNotificationType(value: String): NotificationType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromReminderType(value: ReminderType): String {
        return value.name
    }

    @TypeConverter
    fun toReminderType(value: String): ReminderType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromReminderFrequency(value: ReminderFrequency): String {
        return value.name
    }

    @TypeConverter
    fun toReminderFrequency(value: String): ReminderFrequency {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }
} 