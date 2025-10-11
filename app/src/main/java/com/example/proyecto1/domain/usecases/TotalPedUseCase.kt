package com.example.proyecto1.domain.usecases
import com.example.proyecto1.data.Repositorio

class TotalPedUseCase {
    operator fun invoke(): Int = Repositorio.totalPedidos()
}