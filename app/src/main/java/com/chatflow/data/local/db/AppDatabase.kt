package com.chatflow.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chatflow.data.local.db.ApiKeyEntity

@Database(entities = [ApiKeyEntity::class, ChatEntity::class, MessageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apiKeyDao(): ApiKeyDao
    abstract fun chatDao(): ChatDao
}
