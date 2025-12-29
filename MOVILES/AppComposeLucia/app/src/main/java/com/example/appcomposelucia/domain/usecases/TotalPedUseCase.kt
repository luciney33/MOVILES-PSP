package com.example.appcomposelucia.domain.usecases
import com.example.appcomposelucia.data.Repositorio
import javax.inject.Inject

class TotalPedUseCase @Inject constructor() {
    operator fun invoke(): Int = Repositorio.totalPedidos()
}