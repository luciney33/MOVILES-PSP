package com.example.navigation.ui.menuEntrenamiento.detalleEntrenamiento

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigation.data.constants.Constantes
import com.example.navigation.domain.model.SesionEjercicio
import com.example.navigation.domain.usecases.GetSesionEjercicioById
import com.example.navigation.domain.usecases.UpdateRelacionSeries
import com.example.navigation.domain.usecases.UpdateSesionEjercicio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleEntrenamientoViewModel @Inject constructor(
    private val getSesionEjercicioById: GetSesionEjercicioById,
    private val updateSesionEjercicio: UpdateSesionEjercicio,
    private val updateRelacionSeries: UpdateRelacionSeries
) : ViewModel() {

    var state: MutableLiveData<DetalleEntrenamientoState> = MutableLiveData()
        private set

    fun loadEjercicio(id: Int) {
        try {
            viewModelScope.launch {
                val ej = getSesionEjercicioById(id)
                state.value = DetalleEntrenamientoState(ejercicio = ej)
            }
        }catch (e: Exception) {
            state.value = DetalleEntrenamientoState(mensaje = e.message ?:Constantes.MENSAJE_ERROR_GENERICO)
        }

    }

    fun actualizarEjercicio(actualizado: SesionEjercicio) {

        viewModelScope.launch {

                val res = updateSesionEjercicio(actualizado)
                if (res >= 0) {
                    state.value = DetalleEntrenamientoState(ejercicio = actualizado, guardado = true, mensaje = Constantes.MENSAJE_GUARDADO)
                } else {
                    state.value = DetalleEntrenamientoState(ejercicio = actualizado, guardado = false, mensaje = Constantes.MENSAJE_ERROR_GUARDADO)
                }
        }
    }

    fun actualizar(sesionId: Int, ejercicioId: Int, series: Int, repeticiones: Int) {
        viewModelScope.launch {
                val res = updateRelacionSeries(sesionId, ejercicioId, series, repeticiones)
                if (res >= 0) {
                    val current = state.value?.ejercicio
                    if (current != null) {
                        state.value = DetalleEntrenamientoState(ejercicio = current.copy(series = series, repeticiones = repeticiones), mensaje = Constantes.MENSAJE_RELACION_ACTUALIZADA, guardado = true)
                    } else {
                        state.value = DetalleEntrenamientoState(mensaje = Constantes.MENSAJE_RELACION_ACTUALIZADA, guardado = true)
                    }
                } else {
                    state.value = DetalleEntrenamientoState(mensaje = Constantes.MENSAJE_ERROR_RELACION, guardado = false)
                }

        }
    }

}