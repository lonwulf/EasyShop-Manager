package com.lonwulf.labs.easyshopmanager.domain.model

import com.lonwulf.labs.easyshopmanager.db.Product_catalogue

data class Product(
    val id: String,
    val name: String,
    val desc: String,
    val categoryId: String,
    val subCategoryId: String,
    val price: Double,
    val isBundled: Boolean,
    val quantity: Long,
    val imageUrl: String
)

fun Product_catalogue.toDomain(): Product =
    Product(id, name, desc ?: "", category_id, sub_category_id, price, is_bundled == 1L, quantity ?: 0L, image_url)

fun List<Product_catalogue>.toDomainList(): List<Product> = mutableListOf<Product>().apply {
    this@toDomainList.forEach {
        add(it.toDomain())
    }
}
