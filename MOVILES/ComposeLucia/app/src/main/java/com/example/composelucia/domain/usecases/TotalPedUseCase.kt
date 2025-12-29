package com.example.composelucia.domain.usecases
import com.example.composelucia.data.Repositorio

class TotalPedUseCase {
    operator fun invoke(): Int = Repositorio.totalPedidos()
}