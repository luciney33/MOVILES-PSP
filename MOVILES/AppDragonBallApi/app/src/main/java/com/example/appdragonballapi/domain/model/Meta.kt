package com.example.appdragonballapi.domain.model

data class Meta(
    val currentPage: Int,
    val itemCount: Int,
    val itemsPerPage: Int,
    val totalItems: Int,
    val totalPages: Int
)