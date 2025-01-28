package com.example.flora1.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PeriodDaoTest {
    private lateinit var periodDao: PeriodDao
    private lateinit var periodDatabase: PeriodDatabase

    private var period1 = PeriodEntity(1, "heavy", 1, 11, 2024)
    private var period2 = PeriodEntity(2, "heavy", 2, 11, 2024)

    @Before
    fun createDb(){
        val context: Context = ApplicationProvider.getApplicationContext()

        periodDatabase = Room.inMemoryDatabaseBuilder(context, PeriodDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        periodDao = periodDatabase.dao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        periodDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runTest {
        addOneItemToDb()
        val allItems = periodDao.getAllPeriodLogs().first()
        allItems[0] shouldBe period1
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsTwoItemsIntoDB() = runTest {
        addTwoItemsToDb()
        val allItems = periodDao.getAllPeriodLogs().first()
        allItems[0] shouldBe period1
        allItems[1] shouldBe period2
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateItems_updatesItemsInDB() = runTest {
        addTwoItemsToDb()
        periodDao.savePeriodEntry(period1.copy(day = 4))
        periodDao.savePeriodEntry(period2.copy(day = 5))

        val allItems = periodDao.getAllPeriodLogs().first()
        allItems[0] shouldBe period1.copy(day = 4)
        allItems[1] shouldBe period2.copy(day = 5)
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteItems_deletesAllItemsFromDB() = runTest {
        addTwoItemsToDb()
        periodDao.deletePeriodEntry(period1)
        periodDao.deletePeriodEntry(period2)
        val allItems = periodDao.getAllPeriodLogs().first()
        allItems shouldBe emptyList()
    }

    @Test
    @Throws(Exception::class)
    fun daoGetPeriodForMonth_getsPeriodForMonth() = runTest {
        // given
        val period1 = PeriodEntity(1, "", day = 1, month = 2, year = 2024)
        val period2 = PeriodEntity(2, "", day = 2, month = 2, year = 2023)
        val period3 = PeriodEntity(3, "", day = 17, month = 1, year = 2024)
        periodDao.savePeriodEntry(period1)
        periodDao.savePeriodEntry(period2)
        periodDao.savePeriodEntry(period3)

        // when
        val allItems = periodDao.getPeriodLogsForMonth(2, 2024).first()

        // then
        allItems shouldBe listOf(period1)
    }

    private suspend fun addOneItemToDb() {
        periodDao.savePeriodEntry(period1)
    }

    private suspend fun addTwoItemsToDb() {
        periodDao.savePeriodEntry(period1)
        periodDao.savePeriodEntry(period2)
    }
}
