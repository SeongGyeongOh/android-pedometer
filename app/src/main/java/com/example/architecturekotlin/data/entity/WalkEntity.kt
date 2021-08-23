package com.example.architecturekotlin.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walk_table")
class WalkEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "num") val num: Int,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "count") val count: Int
)