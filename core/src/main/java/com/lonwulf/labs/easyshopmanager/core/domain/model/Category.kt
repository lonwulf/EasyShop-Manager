package com.lonwulf.labs.easyshopmanager.core.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: Long,
    val name: String,
    val image: String? = null,
    val imgVector: ImageVector? = null,
//    val createdAt: Long = System.currentTimeMillis()
)

fun com.lonwulf.labs.easyshopmanager.db.Category.toEntity(): Category =
    Category(id, name, image_url)

fun List<com.lonwulf.labs.easyshopmanager.db.Category>.toEntityList(): List<Category> =
    mutableListOf<Category>().apply {
        this@toEntityList.forEach {
            add(it.toEntity())
        }
    }