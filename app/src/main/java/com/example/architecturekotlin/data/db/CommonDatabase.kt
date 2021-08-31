package com.example.architecturekotlin.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.architecturekotlin.data.entity.CntEntity
import com.example.architecturekotlin.data.entity.WalkEntity

@Database(entities = [CntEntity::class, WalkEntity::class], version = 1, exportSchema = true)
abstract class CommonDatabase: RoomDatabase() {

    abstract fun cntDao(): CntDao
    abstract fun walkDao(): WalkDao

    companion object {

        @Volatile
        private var INSTANCE: CommonDatabase? = null

        fun getDatabase(context: Context): CommonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CommonDatabase::class.java,
                    "common_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

//val MIGRATION_1_2 = object :  Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("ALTER TABLE todo_table RENAME COLUMN title to todoTitle")
//    }
//}