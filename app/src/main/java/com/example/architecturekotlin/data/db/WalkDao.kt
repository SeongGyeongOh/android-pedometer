package com.example.architecturekotlin.data.db

import androidx.room.*
import com.example.architecturekotlin.data.entity.WalkEntity
import com.example.architecturekotlin.presenter.main.walk_fragment.WalkService
import com.example.architecturekotlin.util.common.Logger
import com.example.architecturekotlin.util.common.Pref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Dao
interface WalkDao {
    @Query("SELECT * FROM walk_table")
    fun getWalkCount(): List<WalkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(walkEntity: WalkEntity) : Long

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertAll(vararg walkEntity: WalkEntity) : Long //vararg : 가변인자

    @Query("UPDATE walk_table SET count = :count WHERE date = :date")
    suspend fun updateCnt(count: Int, date: String)

    @Update(entity = WalkEntity::class)
    suspend fun updateCnt2(walkEntity: WalkEntity)

    @Query("DELETE FROM walk_table WHERE  date = :date")
    suspend fun deleteData(date: String)

    @Query("SELECT * FROM walk_table WHERE date = :date")
    fun getTodayCount(date: String) : WalkEntity?

    @Query("SELECT * FROM walk_table WHERE date = :date")
    fun getTodayCountAsFlow(date: String) : Flow<WalkEntity?>

    @Transaction
    suspend fun upsertCnt(walkEntity: WalkEntity) {
        val isExist: WalkEntity? =
            getTodayCount(walkEntity.date)

        if (isExist == null) {
            insert(walkEntity)
        } else {
            updateCnt(walkEntity.count, walkEntity.date)
        }
    }
}