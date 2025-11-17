package com.example.navigation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigation.data.repository.SesionRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sesionRepo: SesionRepositoryImpl
) : ViewModel() {

    private val _state = MutableLiveData(HomeState())
    val state: LiveData<HomeState> = _state

    fun load() {
        viewModelScope.launch {
            val list = sesionRepo.getAllSesionSummaries()
            _state.value = HomeState(sesiones = list)
        }
    }

}
