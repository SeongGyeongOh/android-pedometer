package com.example.architecturekotlin.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.architecturekotlin.data.entity.CntEntity

@Dao
interface CntDao {

    @Query("select * from cnt_table WHERE :id = id")
    suspend fun getCntFromId(id: Int): CntEntity

    @Query("select * from cnt_table order by id DESC limit 1")
    suspend fun getLatestCnt(): List<CntEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCnt(cnt: CntEntity): Long
}