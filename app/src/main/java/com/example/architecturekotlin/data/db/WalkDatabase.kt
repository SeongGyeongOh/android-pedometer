package com.example.architecturekotlin.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.architecturekotlin.data.entity.WalkEntity

@Database(entities = [WalkEntity::class], version = 1, exportSchema = true)
abstract class WalkDatabase : RoomDatabase() {

    abstract fun walkDao(): WalkDao

    companion object {
        @Volatile
        private var INSTANCE: WalkDatabase? = null

        fun getDatabase(context: Context): WalkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WalkDatabase::class.java,
                    "walk_database"
                )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}