package com.lonwulf.labs.easyshopmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.domain.model.Product
import com.lonwulf.labs.easyshopmanager.domain.uiState.CreateProductState
import com.lonwulf.labs.easyshopmanager.domain.useCase.ProductsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProductViewModel(private val productsUseCase: ProductsUseCase) : ViewModel() {

    private var _createProductState: MutableStateFlow<CreateProductState<Long>> =
        MutableStateFlow(CreateProductState.Loading)
    val createProductState
        get() = _createProductState.asStateFlow()

    fun insertProductToCache(
        name: String,
        brandName: String,
        qty: String,
        buyingPrice: Double,
        vendor: String,
        sellingPrice: Double,
        description: String,
        subCategoryId: Long,
        categoryId: Long
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            val product = Product(
                name = name,
                categoryId = categoryId,
                brand = brandName,
                quantity = qty.toLong(),
                buyingPrice = buyingPrice,
                price = sellingPrice,
                vendor = vendor,
                description = description,
                subCategoryId = subCategoryId,
            )
            productsUseCase.insertProduct(product)
                .onStart { _createProductState.value = CreateProductState.Loading }
                .flowOn(Dispatchers.IO)
                .collect { cacheResult ->
                    when (cacheResult) {
                        is CacheResult.Success -> {
                            _createProductState.value = CreateProductState.Success(cacheResult.data)
                        }

                        is CacheResult.Error -> _createProductState.value = CreateProductState.Error(cacheResult.msg)
                    }

                }
        }


    fun validateCreateProduct(
        name: String,
        brandName: String,
        qty: String,
        buyingPrice: Double,
        vendor: String,
        sellingPrice: Double,
        description: String,
        subCategoryId: Long,
    ): Pair<Boolean, String?> {
        val requiredFields = listOf(name, brandName, qty, buyingPrice.toString(), subCategoryId.toString())

        return when {
            requiredFields.any { it.isEmpty() } -> Pair(false, "Please fill in all required fields")
            else -> Pair(true, null)
        }
    }

    fun deleteProduct(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        productsUseCase.deleteProduct(id)
    }
}