package com.example.navigation.ui.menuEntrenamiento.listaEntrenamiento

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigation.domain.usecases.GetEntrenamientosConEjercicios
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ListaEntrenamientoViewModel @Inject constructor(
    private val getEntrenamientosConEjercicios: GetEntrenamientosConEjercicios
) : ViewModel() {

    var state : MutableLiveData<ListaEntrenamientoState> = MutableLiveData()
    private set


    init {
        loadEntrenamientos()
    }

    private fun loadEntrenamientos() {
        viewModelScope.launch {
            try {

                state.value = getEntrenamientosConEjercicios().let { entrenamientos ->
                    ListaEntrenamientoState(entrenamientos = entrenamientos)
                }
            } catch (e: Exception) {
                state.value = ListaEntrenamientoState(mensaje = "Error al cargar los entrenamientos: ${e.message}")
            }
            Log.d("ViewModelDebug", "Datos cargados: ${state.value?.entrenamientos?.size} elementos") // Añade este log
        }
    }
}