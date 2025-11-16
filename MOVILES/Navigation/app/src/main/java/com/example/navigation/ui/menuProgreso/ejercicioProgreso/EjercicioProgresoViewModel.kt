package com.example.navigation.ui.menuProgreso.ejercicioProgreso

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigation.domain.model.Progreso
import com.example.navigation.domain.usecases.GetProgresoEjercicios
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EjercicioProgresoViewModel @Inject constructor(
    private val getProgresoEjercicios: GetProgresoEjercicios
) : ViewModel() {

    private val _state = MutableLiveData(EjercicioProgresoState())
    val state: LiveData<EjercicioProgresoState> = _state

    fun load() {
        viewModelScope.launch {
            val summaries: List<Progreso> = getProgresoEjercicios()
            _state.value = EjercicioProgresoState(items = summaries)
        }
    }

}