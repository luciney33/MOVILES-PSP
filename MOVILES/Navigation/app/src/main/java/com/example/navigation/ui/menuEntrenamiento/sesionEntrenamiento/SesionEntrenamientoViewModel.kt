package com.example.navigation.ui.menuEntrenamiento.sesionEntrenamiento

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigation.data.constants.Constantes
import com.example.navigation.domain.usecases.GetEjerciciosBySesion
import com.example.navigation.domain.usecases.GetSesionById
import com.example.navigation.domain.usecases.UpdateSesionDuracion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SesionEntrenamientoViewModel @Inject constructor(
    private val getSesionById: GetSesionById,
    private val getEjerciciosBySesion: GetEjerciciosBySesion,
    private val updateSesionDuracion: UpdateSesionDuracion
) : ViewModel() {

    var state: MutableLiveData<SesionEntrenamientoState> = MutableLiveData()
        private set
    private var startTime = 0L
    private var elapsedOffset = 0L
    private var tickerJob: Job? = null
    val tiempoFormateado: MutableLiveData<String> = MutableLiveData("00:00:00")

    fun loadSesion(sesionId: Int) {
        viewModelScope.launch {
            try {
                val sesion = getSesionById(sesionId)
                val ejercicios = getEjerciciosBySesion(sesionId)
                state.value = SesionEntrenamientoState(
                    sesion = sesion,
                    ejercicios = ejercicios
                )
                startTimer()
            } catch (e: Exception) {
                state.value = SesionEntrenamientoState(mensaje = e.message ?: Constantes.MENSAJE_ERROR_GENERICO)
            }
        }
    }

    fun startTimer() {
        if (tickerJob?.isActive == true) return
        startTime = SystemClock.elapsedRealtime()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                val now = SystemClock.elapsedRealtime()
                val elapsed = elapsedOffset + (now - startTime)
                tiempoFormateado.postValue(formatMs(elapsed))
                delay(250)
            }
        }
    }

    fun stopTimer() {
        if (tickerJob?.isActive == true) {
            val now = SystemClock.elapsedRealtime()
            elapsedOffset += (now - startTime)
            tickerJob?.cancel()
        }
    }


    fun saveDurationAndStop(sesionId: Int) {
        viewModelScope.launch {
                stopTimer()
                val dur = elapsedOffset
                updateSesionDuracion(sesionId, dur)
        }
    }

    private fun formatMs(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

}
