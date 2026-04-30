package com.lonwulf.labs.easyshopmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonwulf.labs.easyshopmanager.domain.model.Product
import com.lonwulf.labs.easyshopmanager.domain.useCase.ProductsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProductViewModel(private val productsUseCase: ProductsUseCase) : ViewModel() {


    fun insertProductToCache(
        name: String,
        brandName:String,
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
                .onStart { }
                .flowOn(Dispatchers.IO)
                .collect {

                }
        }


    fun deleteProduct(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        productsUseCase.deleteProduct(id)
    }
}