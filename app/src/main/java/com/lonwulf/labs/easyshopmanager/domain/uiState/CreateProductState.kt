package com.lonwulf.labs.easyshopmanager.domain.uiState

sealed class CreateProductState<out T> {
    data object Loading : CreateProductState<Nothing>()
    data class Success<T>(val data: T) : CreateProductState<T>()
    data class Error(val message: String) : CreateProductState<Nothing>()
}