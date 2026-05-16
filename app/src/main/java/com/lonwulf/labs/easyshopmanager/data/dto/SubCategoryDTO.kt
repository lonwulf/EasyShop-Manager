package com.lonwulf.labs.easyshopmanager.data.dto

import com.lonwulf.labs.easyshopmanager.domain.model.SubCategory
import kotlinx.serialization.Serializable

@Serializable
data class SubCategoryDTO(
    val id: Long? = null,
    val name: String? = null,
    val categoryId: Long? = null
)

fun SubCategoryDTO.toDomain(): SubCategory = SubCategory(
    id = this.id ?: 0L,
    name = name ?: "",
    categoryId = categoryId ?: 0L
)
