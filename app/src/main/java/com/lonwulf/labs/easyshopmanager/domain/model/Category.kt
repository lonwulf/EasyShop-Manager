package com.lonwulf.labs.easyshopmanager.domain.model

import com.lonwulf.labs.easyshopmanager.db.Category

data class Category(
    val id: String,
    val name: String,
//    val createdAt: Long = System.currentTimeMillis()
)

fun Category.toDomain(): com.lonwulf.labs.easyshopmanager.domain.model.Category =
    com.lonwulf.labs.easyshopmanager.domain.model.Category(id, name)

fun List<Category>.toDomainList(): List<com.lonwulf.labs.easyshopmanager.domain.model.Category> =
    mutableListOf<com.lonwulf.labs.easyshopmanager.domain.model.Category>().apply {
        this@toDomainList.forEach {
            add(it.toDomain())
        }
    }