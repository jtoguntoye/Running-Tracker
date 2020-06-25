package com.example.runningtracker.UI.Fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningtracker.R
import com.example.runningtracker.UI.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragments: Fragment(R.layout.fragment_tracking){

    private val mainViewModel: MainViewModel by viewModels()
}