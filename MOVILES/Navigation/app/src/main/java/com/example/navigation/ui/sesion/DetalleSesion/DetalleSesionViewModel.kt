package com.example.navigation.ui.sesion.DetalleSesion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigation.data.repository.SesionRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleSesionViewModel @Inject constructor(
    private val sesionRepo: SesionRepositoryImpl
) : ViewModel() {

    private val _state = MutableLiveData(DetalleSesionState())
    val state: LiveData<DetalleSesionState> = _state

    fun load(sesionId: Int) {
        _state.value = DetalleSesionState(isLoading = true)
        viewModelScope.launch {
            try {
                val sesion = sesionRepo.getSesionById(sesionId)
                val ejercicios = sesionRepo.getEjerciciosBySesionId(sesionId)
                val entrenamientoNombre = sesion?.let { sesionRepo.getEntrenamientoName(it.entrenamientoId) } ?: ""
                _state.value = DetalleSesionState(isLoading = false, sesion = sesion, ejercicios = ejercicios, entrenamientoNombre = entrenamientoNombre)
            } catch (e: Exception) {
                _state.value = DetalleSesionState(isLoading = false, error = e.message)
            }
        }
    }

}