package com.example.navigation.ui.menuProgreso.registroProgreso

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigation.data.local.dao.ProgresoDao
import com.example.navigation.data.local.entity.ProgresoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroProgresoViewModel @Inject constructor(
    private val progresoDao: ProgresoDao
) : ViewModel() {

    private val _state = MutableLiveData(RegistroProgresoState())
    val state: LiveData<RegistroProgresoState> = _state

    fun saveProgreso(fechaMs: Long, pesoKg: Double, grasaPercent: Double, notas: String) {
        // set loading
        _state.value = RegistroProgresoState(isLoading = true)
        viewModelScope.launch {
            try {
                val entity = ProgresoEntity(
                    id = 0,
                    fecha = fechaMs,
                    pesoKg = pesoKg,
                    grasaPercent = grasaPercent,
                    notas = notas
                )
                progresoDao.insert(entity)
                _state.postValue(RegistroProgresoState(isLoading = false, success = true, message = "Guardado"))
            } catch (e: Exception) {
                _state.postValue(RegistroProgresoState(isLoading = false, success = false, error = e.message ?: "Error"))
            }
        }
    }

    fun clearState() {
        _state.value = RegistroProgresoState()
    }

}