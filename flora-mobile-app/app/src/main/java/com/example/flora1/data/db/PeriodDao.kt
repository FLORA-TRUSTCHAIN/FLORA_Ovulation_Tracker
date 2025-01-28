package com.example.flora1.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

@Dao
interface PeriodDao {


    @Upsert
    suspend fun savePeriodEntry(food : PeriodEntity)

    @Delete
    suspend fun deletePeriodEntry(food : PeriodEntity)

    @Query("SELECT * FROM period")
    fun getAllPeriodLogs() : Flow<List<PeriodEntity>>

    @Query("SELECT * FROM period WHERE id = :id")
    fun getPeriodLogById(id : Int) : Flow<PeriodEntity>

    @Query("SELECT * FROM period WHERE month = :specifiedMonth AND year = :specifiedYear ORDER BY day ASC")
    fun getPeriodLogsForMonth(specifiedMonth: Int, specifiedYear: Int): Flow<List<PeriodEntity>>
}
