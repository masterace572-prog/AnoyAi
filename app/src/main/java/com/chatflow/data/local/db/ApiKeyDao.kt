package com.chatflow.data.local.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiKeyDao {
    @Query("SELECT * FROM api_keys ORDER BY addedAt DESC")
    fun getAllKeys(): Flow<List<ApiKeyEntity>>

    @Query("SELECT * FROM api_keys WHERE id = :id")
    suspend fun getKeyById(id: String): ApiKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(apiKey: ApiKeyEntity)

    @Delete
    suspend fun deleteKey(apiKey: ApiKeyEntity)

    @Query("DELETE FROM api_keys")
    suspend fun clearAllKeys()
}
