package com.example.architecturekotlin.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.architecturekotlin.data.entity.WalkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkDao {
    @Query("SELECT * FROM walk_table")
    fun getWalkCount(): Flow<List<WalkEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(walkCount: WalkEntity) : Long

    @Query("DELETE FROM walk_table WHERE :date = date")
    suspend fun deleteData(date: String)

    @Query("SELECT * FROM walk_table WHERE :date = date")
    fun getTodayCount(date: String): Flow<List<WalkEntity>>
}