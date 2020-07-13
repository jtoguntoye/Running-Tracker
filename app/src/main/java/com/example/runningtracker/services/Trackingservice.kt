package com.example.runningtracker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runningtracker.R
import com.example.runningtracker.UI.MainActivity
import com.example.runningtracker.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtracker.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runningtracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtracker.other.Constants.ACTION_STOP_SERVICE
import com.example.runningtracker.other.Constants.FAST_LOCATION_INTERVAL
import com.example.runningtracker.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runningtracker.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runningtracker.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runningtracker.other.Constants.NOTIFICATION_ID
import com.example.runningtracker.other.Constants.TIMER_UPDATE_INTERVAL
import com.example.runningtracker.other.TrackingUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

    typealias  polyLine = MutableList<LatLng>
    typealias  polyLines = MutableList<polyLine>

    class TrackingService: LifecycleService() {

        var isFirstRun = true

        lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        private val timeRunInSeconds = MutableLiveData<Long>()


    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<polyLines>()
    }

        private fun postInitialValues() {
            isTracking.postValue(false)
            pathPoints.postValue(mutableListOf())
            timeRunInSeconds.postValue(0L)
            timeRunInMillis.postValue(0L)
        }

        override fun onCreate() {
            super.onCreate()
            postInitialValues()
            fusedLocationProviderClient = FusedLocationProviderClient(this)

            isTracking.observe(this, Observer {
                updateLocationTracking(it)
            })
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    }else {
                        Timber.d("Resuming service")
                        startTimer()
                    }

                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
        }

        private var isTimerEnabled = false
        private var lapTime = 0L
        private var totalTimeRun = 0L
        private var timeStarted = 0L
        private var lastSecondTimeStamp = 0L


        private fun startTimer() {
            addEmptyPolyline()
            isTracking.postValue(true)
            timeStarted = System.currentTimeMillis()
            isTimerEnabled = true
            //launch a coroutine to track the current time
            CoroutineScope(Dispatchers.Main).launch {
                while(isTracking.value!!) {
                    //time difference between now and time run was started
                    lapTime = System.currentTimeMillis() - timeStarted

                    timeRunInMillis.postValue(totalTimeRun + lapTime)

                    if(timeRunInMillis.value!! >lastSecondTimeStamp +1000L) {
                        timeRunInSeconds.postValue(timeRunInSeconds.value!! +1 )
                        lastSecondTimeStamp+=1000L
                    }
                    delay(TIMER_UPDATE_INTERVAL)
                }
                totalTimeRun +=lapTime
            }
        }


        private fun pauseService () {
            isTracking.postValue(false)
            isTimerEnabled = false
        }

        @SuppressLint("MissingPermission")
        private fun updateLocationTracking(isTracking: Boolean) {
            if(isTracking) {
                if(TrackingUtils.hasLocationPermissions(this)) {
                    val request = LocationRequest().apply {
                        interval = LOCATION_UPDATE_INTERVAL
                        fastestInterval = FAST_LOCATION_INTERVAL
                        priority = PRIORITY_HIGH_ACCURACY
                    }
                    fusedLocationProviderClient.requestLocationUpdates(
                        request,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }

            } else
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }

        //create the location callback object that will be used to get the loation from fusedLocationProvider
         val  locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                super.onLocationResult(result)
                if(isTracking.value!!) {
                    result?.locations?.let {locations->
                        for(location in locations) {
                            addPathPoints(location)
                            Timber.d("New location ${location.latitude}, ${location.longitude}")
                        }
                    }
                }
            }
        }

        private  fun addPathPoints(location: Location?){
            location?.let {
                val pos = LatLng(location.latitude, location.longitude)
                pathPoints.value?.apply {
                    last().add(pos)
                    pathPoints.postValue(this)
                }
            }
        }



        //function to add empty list after you pause a run before you start tracking again
        private fun addEmptyPolyline() = pathPoints.value?.apply {
            add(mutableListOf())
            pathPoints.postValue(this)
        } ?: pathPoints.postValue(mutableListOf(mutableListOf()))



    private fun startForegroundService() {
        startTimer()
        addEmptyPolyline()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        val  notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())


    }

    private  fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
           IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}
