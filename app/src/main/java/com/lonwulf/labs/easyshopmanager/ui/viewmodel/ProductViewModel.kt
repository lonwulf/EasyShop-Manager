package com.lonwulf.labs.easyshopmanager.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonwulf.labs.easyshopmanager.data.source.db.CacheResult
import com.lonwulf.labs.easyshopmanager.domain.model.Product
import com.lonwulf.labs.easyshopmanager.domain.model.fold
import com.lonwulf.labs.easyshopmanager.domain.uiState.BrandsState
import com.lonwulf.labs.easyshopmanager.domain.uiState.CategoriesState
import com.lonwulf.labs.easyshopmanager.domain.uiState.CreateProductState
import com.lonwulf.labs.easyshopmanager.domain.uiState.SubCategoriesState
import com.lonwulf.labs.easyshopmanager.domain.useCase.BrandsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.CategoriesSubCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.ProductsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productsUseCase: ProductsUseCase,
    private val brandUseCase: BrandsUseCase,
    private val categoriesSubCategoriesUseCase: CategoriesSubCategoriesUseCase,
) : ViewModel() {

    private var _createProductState: MutableStateFlow<CreateProductState<Long>> =
        MutableStateFlow(CreateProductState.Loading)
    val createProductState
        get() = _createProductState.asStateFlow()

    private val _brandsState = MutableStateFlow(BrandsState())
    val brandsState
        get() = _brandsState.asStateFlow()
    private val _categoriesState = MutableStateFlow(CategoriesState())
    val categoriesState
        get() = _categoriesState.asStateFlow()
    private val _subCategoriesState = MutableStateFlow(SubCategoriesState())
    val subCategoriesState
        get() = _subCategoriesState.asStateFlow()

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

    fun fetchCachedBrands() = viewModelScope.launch {
        brandUseCase()
            .onStart { }
            .flowOn(Dispatchers.IO)
            .collect { resource ->
                resource.fold(
                    onLoading = { _brandsState.value = BrandsState(isLoading = true) },
                    onSuccess = { _brandsState.value = BrandsState(brands = it) },
                    onFailure = { message, _ -> _brandsState.value = BrandsState(error = message) })
                Log.e("MainVm: ", "${brandsState.value.brands}")
            }
    }

    fun fetchCachedCategories() = viewModelScope.launch {
        categoriesSubCategoriesUseCase.getCategories()
            .onStart { }
            .flowOn(Dispatchers.IO)
            .collect { resource ->
                resource.fold(
                    onLoading = { _categoriesState.value = CategoriesState(isLoading = true) },
                    onSuccess = { _categoriesState.value = CategoriesState(categories = it) },
                    onFailure = { message, _ -> _categoriesState.value = CategoriesState(error = message) })
                Log.e("MainVm: ", "${categoriesState.value.categories}")
            }
    }

    fun fetchCachedSubCategories() = viewModelScope.launch {
        categoriesSubCategoriesUseCase.getSubCategories()
            .onStart { }
            .flowOn(Dispatchers.IO)
            .collect { resource ->
                resource.fold(
                    onLoading = { _subCategoriesState.value = SubCategoriesState(isLoading = true) },
                    onSuccess = { _subCategoriesState.value = SubCategoriesState(subCategories = it) },
                    onFailure = { message, _ -> _subCategoriesState.value = SubCategoriesState(error = message) })
                Log.e("MainVm: ", "${_subCategoriesState.value.subCategories}")
            }
    }

    fun fetchSubCategoriesByCategoryId(categoryId: Long) = viewModelScope.launch {
        categoriesSubCategoriesUseCase.getSubCategoriesByCategory(categoryId)
            .onStart { }
            .flowOn(Dispatchers.IO)
            .collect { resource ->
                resource.fold(
                    onLoading = { _subCategoriesState.value = SubCategoriesState(isLoading = true) },
                    onSuccess = { _subCategoriesState.value = SubCategoriesState(subCategories = it) },
                    onFailure = { message, _ -> _subCategoriesState.value = SubCategoriesState(error = message) })
                Log.e("MainVm: ", "${_subCategoriesState.value.subCategories}")
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