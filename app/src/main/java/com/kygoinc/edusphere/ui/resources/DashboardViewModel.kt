package com.kygoinc.edusphere.ui.resources

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is resources Fragment"
    }
    val text: LiveData<String> = _text
}