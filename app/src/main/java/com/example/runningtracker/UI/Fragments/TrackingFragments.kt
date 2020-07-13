package com.example.runningtracker.UI.Fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runningtracker.R
import com.example.runningtracker.UI.MainViewModel
import com.example.runningtracker.other.Constants
import com.example.runningtracker.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtracker.other.Constants.MAP_ZOOM
import com.example.runningtracker.other.Constants.POLYLINE_COLOR
import com.example.runningtracker.other.Constants.POLYLINE_WIDTH
import com.example.runningtracker.services.TrackingService
import com.example.runningtracker.services.polyLine
import com.example.runningtracker.services.polyLines
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*

@AndroidEntryPoint
class TrackingFragments: Fragment(R.layout.fragment_tracking){

    private val mainViewModel: MainViewModel by viewModels()

    private var map: GoogleMap? = null

    private var isTracking = false
    private var pathPoints = mutableListOf<polyLine>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        btnToggleRun.setOnClickListener{
           toggleRun()
        }
        mapView.getMapAsync{
            map = it
            addAllPolylines()
        }

        subscribeToObserve()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }


    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun subscribeToObserve() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            UpdateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyLine()
            moveCameraToUser()
        })
    }

    private fun toggleRun() {
        if(isTracking)
        {
            sendcommandToService(ACTION_PAUSE_SERVICE)
        } else{
            sendcommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun UpdateTracking(isTracking:Boolean) {
        this.isTracking = isTracking
        if(!isTracking){
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        } else {
            btnToggleRun.text ="Stop"
            btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToUser() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines() {
         for(polyLine in  pathPoints) {
             val polylineOptions = PolylineOptions()
                 .color(POLYLINE_COLOR)
                 .width(POLYLINE_WIDTH)
                 .addAll(polyLine)
                map?.addPolyline(polylineOptions)
         }
    }


    private fun addLatestPolyLine() {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1 ) {
            val PreLastpolyLine = pathPoints.last()[pathPoints.last().size - 2]
            val lastPolyline = pathPoints.last().last()

            val polyLineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(PreLastpolyLine)
                .add(lastPolyline)

            map?.addPolyline(polyLineOptions)

        }
    }
    //function to send intent to the tracing service class
    private fun sendcommandToService(action:String) =
        Intent(requireContext(), TrackingService::class.java).also{
            it.action = action
            requireContext().startService(it)
        }

    }
