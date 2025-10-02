package com.example.proyecto1.ui.pantallamain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.sql.Date
import java.time.LocalDate

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
        _state.value=state.value?.copy(stateCorreo = correo)
    }
    fun comentarioEscrito(comentario: String) {
        _state.value=state.value?.copy(stateComentario = comentario)
    }
    fun telfEscrito(numTelf: Int) {
        _state.value=state.value?.copy(stateTelf = numTelf)
    }
    fun fechanacEscrito(fecha: LocalDate) {
        _state.value=state.value?.copy(stateFechaNac = fecha)
    }
    fun sexoSeleccionado(sexo: String){
        _state.value=state.value?.copy(stateSexo = sexo )
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