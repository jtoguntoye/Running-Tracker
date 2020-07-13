package com.example.runningtracker.other

import android.graphics.Color
import android.graphics.Color.RED

object Constants {
    const val RUNNING_DATABASE_NAME = "running_db"
    val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val TIMER_UPDATE_INTERVAL = 50L

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID = "TRACKING_CHANNEL_ID"
    const val NOTIFICATION_CHANNEL_NAME = "TRACKING_NOTIFICATION"
    const val NOTIFICATION_ID = 1

    const val LOCATION_UPDATE_INTERVAL =5000L
    const val FAST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_COLOR = RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f
}