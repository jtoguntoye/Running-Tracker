package com.example.runningtracker.DB

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run
    (val img:Bitmap? = null,
     var timestamp:Long = 0L,
     var aveSpeedInKMH: Float = 0f,
     var distanceInMeters: Int = 0,
     var timeInMillis: Long = 0L,
     var caloriesBurnt: Int = 0)   {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

}