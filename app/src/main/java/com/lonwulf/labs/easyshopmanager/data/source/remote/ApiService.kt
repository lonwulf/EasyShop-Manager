package com.lonwulf.labs.easyshopmanager.data.source.remote

import com.lonwulf.labs.easyshopmanager.data.dto.APIResponse
import com.lonwulf.labs.easyshopmanager.data.dto.CategoryDTO
import com.lonwulf.labs.easyshopmanager.data.dto.SubCategoryDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.reflect.typeInfo

interface IApiService {
    suspend fun fetchCategories(): APIResponse<List<CategoryDTO>>
    suspend fun fetchSubCategories(): APIResponse<List<SubCategoryDTO>>
}

class ApiServiceImpl(private val client: HttpClient): IApiService {
    override suspend fun fetchCategories(): APIResponse<List<CategoryDTO>> =
        client.get("categories"){}
            .body(typeInfo<APIResponse<List<CategoryDTO>>>())

    override suspend fun fetchSubCategories(): APIResponse<List<SubCategoryDTO>> =
        client.get("subcategories"){}
            .body(typeInfo<APIResponse<List<SubCategoryDTO>>>())

}