package com.example.proyecto1.ui.pantallamain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.sql.Date

class MainViewModel : ViewModel() {
    private var _state : MutableLiveData<MainState> = MutableLiveData(MainState())
    val state : LiveData<MainState> get() = _state

    fun nombreEscrito(nombre: String) {
        _state.value=state.value?.copy(stateNombre = nombre )

    }
    fun apellidoEscrito(apellido: String) {
        _state.value=state.value?.copy(stateApellido = apellido)

    }
    fun correoEscrito(correo: String) {

    }
    fun comentarioEscrito(comentario: String) {

    }
    fun telfEscrito(numTelf: Int) {

    }
    fun fechanacEscrito(fecha: Date) {

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