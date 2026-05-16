package com.lonwulf.labs.easyshopmanager.data.dto

import com.lonwulf.labs.easyshopmanager.domain.model.Category
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDTO(
    val id: Long? = null,
    val name: String? = null,
    val imageUrl: String? = null

)

fun CategoryDTO.toDomain(): Category = Category(
    id = this.id ?: 0L,
    name = name ?: "",
    image = imageUrl ?: ""
)