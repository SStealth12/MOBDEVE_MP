package com.mobdeve.s13.estanol.miguelfrancis.mp.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LeaderboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Leaderboard Fragment"
    }
    val text: LiveData<String> = _text
}