package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.status

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StatusViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Status Fragment"
    }
    val text: LiveData<String> = _text
}