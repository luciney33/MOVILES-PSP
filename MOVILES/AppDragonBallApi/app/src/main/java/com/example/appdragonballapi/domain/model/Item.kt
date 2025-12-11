package com.example.appdragonballapi.domain.model

data class Item(
    val deletedAt: Any,
    val description: String,
    val id: Int,
    val image: String,
    val isDestroyed: Boolean,
    val name: String
)