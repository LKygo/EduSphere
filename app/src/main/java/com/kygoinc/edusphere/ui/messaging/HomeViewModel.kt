package com.kygoinc.edusphere.ui.messaging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is messaging Fragment"
    }
    val text: LiveData<String> = _text
}