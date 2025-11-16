package com.example.navigation.ui.menuEntrenamiento.listaEntrenamiento

import com.example.navigation.domain.model.Entrenamiento


data class ListaEntrenamientoState(
    val entrenamientos : List<Entrenamiento> = emptyList(),
    val mensaje : String? = null

)
