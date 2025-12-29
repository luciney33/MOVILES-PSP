package com.example.appcomposelucia.domain.usecases
import com.example.appcomposelucia.data.Repositorio

class TotalPedUseCase {
    operator fun invoke(): Int = Repositorio.totalPedidos()
}