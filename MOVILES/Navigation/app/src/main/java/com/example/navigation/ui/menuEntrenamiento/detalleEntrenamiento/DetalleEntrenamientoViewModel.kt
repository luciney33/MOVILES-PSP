package com.example.navigation.ui.menuEntrenamiento.detalleEntrenamiento

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        viewModelScope.launch {
            try {
                val ej = getSesionEjercicioById(id)
                state.value = DetalleEntrenamientoState(ejercicio = ej)
            } catch (e: Exception) {
                Log.w("DetalleVM","Error cargando ejercicio id=$id", e)
                state.value = DetalleEntrenamientoState(mensaje = "No se pudo cargar el ejercicio: ${e.message}")
            }
        }
    }

    fun actualizarEjercicio(actualizado: SesionEjercicio) {
        viewModelScope.launch {
            try {
                val res = updateSesionEjercicio(actualizado)
                if (res >= 0) {
                    state.value = DetalleEntrenamientoState(ejercicio = actualizado, guardado = true, mensaje = "Guardado")
                } else {
                    state.value = DetalleEntrenamientoState(ejercicio = actualizado, guardado = false, mensaje = "Error guardando")
                }
            } catch (e: Exception) {
                Log.w("DetalleVM","Error actualizando ejercicio id=${actualizado.id}", e)
                state.value = DetalleEntrenamientoState(ejercicio = actualizado, guardado = false, mensaje = "Error: ${e.message}")
            }
        }
    }

    fun actualizar(sesionId: Int, ejercicioId: Int, series: Int, repeticiones: Int) {
        viewModelScope.launch {
            try {
                val res = updateRelacionSeries(sesionId, ejercicioId, series, repeticiones)
                if (res >= 0) {
                    // actualizamos el state para reflejar el cambio (opcional)
                    val current = state.value?.ejercicio
                    if (current != null) {
                        state.value = DetalleEntrenamientoState(ejercicio = current.copy(series = series, repeticiones = repeticiones), mensaje = "Relación actualizada", guardado = true)
                    } else {
                        state.value = DetalleEntrenamientoState(mensaje = "Relación actualizada", guardado = true)
                    }
                } else {
                    state.value = DetalleEntrenamientoState(mensaje = "Error actualizando relación", guardado = false)
                }
            } catch (e: Exception) {
                Log.w("DetalleVM","Error actualizando relacion series", e)
                state.value = DetalleEntrenamientoState(mensaje = "Error actualizando relación: ${e.message}", guardado = false)
            }
        }
    }

}