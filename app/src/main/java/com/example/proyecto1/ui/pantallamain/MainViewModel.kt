package com.example.proyecto1.ui.pantallamain

import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.sql.Date

class MainViewModel : ViewModel() {
    private var _state : MutableLiveData<MainState> = MutableLiveData(MainState())
    val state : LiveData<MainState> get() = _state

    fun nombreEscrito(toString: String) {

    }
    fun apellidoEscrito(toString: String) {

    }
    fun correoEscrito(toString: String) {

    }
    fun comentarioEscrito(toString: String) {

    }
    fun telfEscrito(toInt: Int) {

    }
    fun fechanacEscrito(toDate: Date) {

    }
    fun mujerChecked(checked: Boolean) {

    }
    fun hombreChecked(checked: Boolean) {

    }
    fun otroChecked(checked: Boolean) {

    }
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