package com.example.runningtracker.DB

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun (run:Run)

    @Query( "SELECT * FROM running_table ORDER BY timestamp DESC" )
    fun getAllRunsSortedByDate() : LiveData<List<Run>>

    @Query( "SELECT * FROM running_table ORDER BY timeInMillis DESC" )
    fun getAllRunsSortedByTimeInMillis() : LiveData<List<Run>>

    @Query( "SELECT * FROM running_table ORDER BY caloriesBurnt DESC" )
    fun getAllRunsSortedBycaloriesBurnt() : LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY aveSpeedInKMH DESC")
    fun getAllRunsSortedByAveSpeed() : LiveData<List<Run>>

    @Query( "SELECT * FROM running_table ORDER BY distanceInMeters DESC" )
    fun getAllRunsSortedByDistance() : LiveData<List<Run>>



    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): LiveData<Long>


    @Query("SELECT SUM(caloriesBurnt) FROM running_table")
    fun getTotalCaloriesBurnt(): LiveData<Int>


    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(aveSpeedInKMH) FROM running_table")
    fun getAvgSpeed(): LiveData<Float>

}
