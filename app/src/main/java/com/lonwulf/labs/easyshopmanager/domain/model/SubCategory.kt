package com.lonwulf.labs.easyshopmanager.domain.model

import com.lonwulf.labs.easyshopmanager.db.Sub_category

data class SubCategory(
    val id: String,
    val name: String,
    val categoryId: String,
//    val createdAt: Long = System.currentTimeMillis()
)

fun Sub_category.toDomain(): SubCategory = SubCategory(id, name, category_id)

fun List<Sub_category>.toDomainList(): List<SubCategory> = mutableListOf<SubCategory>().apply {
    this@toDomainList.forEach {
        add(it.toDomain())
    }
}