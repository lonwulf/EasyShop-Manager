package com.lonwulf.labs.easyshopmanager.data.source.remote

import com.lonwulf.labs.easyshopmanager.data.dto.APIResponse
import com.lonwulf.labs.easyshopmanager.data.dto.CategoryDTO
import com.lonwulf.labs.easyshopmanager.data.dto.SubCategoryDTO
import com.lonwulf.labs.easyshopmanager.data.network.APIResult
import com.lonwulf.labs.easyshopmanager.data.network.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher

class AppRemoteDataSource(private val apiService: IApiService) : RemoteDataSource() {

    suspend fun fetchCategories(dispatcher: CoroutineDispatcher): APIResult<APIResponse<List<CategoryDTO>>> =
        safeApiCall(dispatcher) {
            apiService.fetchCategories()
        }

    suspend fun fetchSubCategories(dispatcher: CoroutineDispatcher): APIResult<APIResponse<List<SubCategoryDTO>>> =
        safeApiCall(dispatcher) {
            apiService.fetchSubCategories()
        }
}