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

fun Category.toDomain(): com.lonwulf.labs.easyshopmanager.domain.model.Category =
    com.lonwulf.labs.easyshopmanager.domain.model.Category(id, name, image)

fun List<Category>.toDomainList(): List<com.lonwulf.labs.easyshopmanager.domain.model.Category> =
    mutableListOf<com.lonwulf.labs.easyshopmanager.domain.model.Category>().apply {
        this@toDomainList.forEach {
            add(it.toDomain())
        }
    }