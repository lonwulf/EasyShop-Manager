package com.lonwulf.labs.easyshopmanager.domain.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.lonwulf.labs.easyshopmanager.db.Category

data class Category(
    val id: Long,
    val name: String,
    val image: String? = null,
    val imgVector: ImageVector? = null,
//    val createdAt: Long = System.currentTimeMillis()
)

fun Category.toEntity(): com.lonwulf.labs.easyshopmanager.domain.model.Category =
    com.lonwulf.labs.easyshopmanager.domain.model.Category(id, name, image_url)

fun List<Category>.toEntityList(): List<com.lonwulf.labs.easyshopmanager.domain.model.Category> =
    mutableListOf<com.lonwulf.labs.easyshopmanager.domain.model.Category>().apply {
        this@toEntityList.forEach {
            add(it.toEntity())
        }
    }