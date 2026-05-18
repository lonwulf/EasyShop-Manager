package com.lonwulf.labs.easyshopmanager.data.dto

import com.lonwulf.labs.easyshopmanager.domain.model.SubCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubCategoryDTO(
    val id: Long? = null,
    val name: String? = null,
    @SerialName("category_id") val categoryId: Long? = null,
    @SerialName("image_url") val imageUrl: String? = null
)

fun SubCategoryDTO.toDomain(): SubCategory = SubCategory(
    id = this.id ?: 0L,
    name = this.name ?: "",
    categoryId = this.categoryId ?: 0L,
    imageUrl = this.imageUrl ?: ""
)
