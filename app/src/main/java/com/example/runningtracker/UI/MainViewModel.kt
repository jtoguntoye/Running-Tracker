package com.example.runningtracker.UI

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.runningtracker.Repositories.MainRepositories
import javax.inject.Inject

class MainViewModel @ViewModelInject constructor(
    val mainRepositories: MainRepositories
): ViewModel() {
}