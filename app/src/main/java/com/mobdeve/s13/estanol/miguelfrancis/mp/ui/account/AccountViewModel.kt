package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Account Fragment"
    }
    val text: LiveData<String> = _text
}
