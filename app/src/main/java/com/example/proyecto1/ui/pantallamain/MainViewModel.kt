package com.example.proyecto1.ui.pantallamain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private var _state : MutableLiveData<MainState> = MutableLiveData(MainState())
    val state : LiveData<MainState> get() = _state
}