package com.example.proyecto1.ui.pantallamain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.sql.Date
import java.time.LocalDate

class MainViewModel : ViewModel() {
    private var _state : MutableLiveData<MainState> = MutableLiveData(MainState())
    val state : LiveData<MainState> get() = _state
    fun btnAntClicked() {

    }
    fun btnSigClicked() {

    }
    fun btnLimpClicked() {

    }
    fun btnActClicked() {

    }
    fun btnBorrarClicked() {

    }
    fun btnGuardarClicked() {

    }
}