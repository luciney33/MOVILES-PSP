package com.example.navigation.ui.menuEntrenamiento.listaEntrenamiento

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigation.data.constants.Constantes
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


    private fun loadEntrenamientos() {
        try {
            viewModelScope.launch {
                state.value = getEntrenamientosConEjercicios().let { entrenamientos ->
                    ListaEntrenamientoState(entrenamientos = entrenamientos)
                }
            }
        }catch (e: Exception) {
            state.value = ListaEntrenamientoState(mensaje = e.message ?: Constantes.MENSAJE_ERROR_GENERICO)
        }

    }

    init {
        loadEntrenamientos()
    }
}