package com.example.flora1.data.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [PeriodEntity::class],
    version = 1
)
abstract class PeriodDatabase : RoomDatabase() {
    abstract fun dao() : PeriodDao

}
