package com.example.runningtracker.Repositories

import com.example.runningtracker.DB.Run
import com.example.runningtracker.DB.RunDao
import javax.inject.Inject

class MainRepositories @Inject constructor(
    val runDao: RunDao) {

    suspend fun insertRun (run:Run)= runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByAveSpeed() = runDao.getAllRunsSortedByAveSpeed()

    fun getAllRunsSortedByCaloriesBurnt() = runDao.getAllRunsSortedBycaloriesBurnt()

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()

    fun getTotalCaloriesBurnt() = runDao.getTotalCaloriesBurnt()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

}