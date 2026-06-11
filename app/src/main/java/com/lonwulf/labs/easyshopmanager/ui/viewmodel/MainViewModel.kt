package com.lonwulf.labs.easyshopmanager.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonwulf.labs.easyshopmanager.core.domain.model.fold
import com.lonwulf.labs.easyshopmanager.domain.uiState.CategoriesState
import com.lonwulf.labs.easyshopmanager.domain.uiState.ProductState
import com.lonwulf.labs.easyshopmanager.domain.useCase.CategoriesSubCategoriesUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.FetchProductsUseCase
import com.lonwulf.labs.easyshopmanager.domain.useCase.UserPrefsUseCase
import com.lonwulf.labs.easyshopmanager.navigation.Destinations
import com.lonwulf.labs.easyshopmanager.navigation.TopLevelDestinations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val fetchProductsUseCase: FetchProductsUseCase,
    private val fetchCategoriesUseCase: CategoriesSubCategoriesUseCase,
    internal val userPrefsUseCase: UserPrefsUseCase,
) : ViewModel() {
    private val _productsState = MutableStateFlow(ProductState())
    val productsState
        get() = _productsState.asStateFlow()

    private val _categoriesState = MutableStateFlow(CategoriesState())
    val categoriesState
        get() = _categoriesState.asStateFlow()

    val startDestination = userPrefsUseCase()
        .map {
            if (it.token.isNotBlank()) {
                TopLevelDestinations.HomeScreen.route
            } else {
                Destinations.SignInScreen.route
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TopLevelDestinations.HomeScreen.route
        )

    init {
        fetchCachedProducts()
        fetchCachedCategories()
    }

    fun fetchCachedProducts() = viewModelScope.launch(Dispatchers.IO) {
        fetchProductsUseCase()
            .onStart { }
            .flowOn(Dispatchers.IO)
            .collect {
                _productsState.value = it
                Log.e("MainVm: ", "${it.products}")
            }
    }

    fun fetchCachedCategories() = viewModelScope.launch {
        fetchCategoriesUseCase.getCategories()
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
}