package com.example.flora1.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "period")
data class PeriodEntity (
    @PrimaryKey(autoGenerate = true) val id :Int = 0,
    val flow : String,
    val day :Int,
    val month: Int,
    val year : Int,
)
