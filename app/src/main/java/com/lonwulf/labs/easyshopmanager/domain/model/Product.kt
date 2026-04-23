package com.lonwulf.labs.easyshopmanager.domain.model

import com.lonwulf.labs.easyshopmanager.db.Product_catalogue
import com.lonwulf.labs.easyshopmanager.db.SelectProductsWithCategory

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val categoryId: String,
    val subCategoryId: String,
    val price: Double,
    val isBundled: Boolean,
    val quantity: Long,
    val imageUrl: String,
    val serialNo: String,
)

fun Product_catalogue.toDomain(): Product =
    Product(
        id,
        name,
        description ?: "",
        category_id,
        sub_category_id,
        price,
        is_bundled == 1L,
        quantity ?: 0L,
        image_url,
        serial_no ?: ""
    )

fun List<Product_catalogue>.toDomainList(): List<Product> = mutableListOf<Product>().apply {
    this@toDomainList.forEach {
        add(it.toDomain())
    }
}

fun SelectProductsWithCategory.toDomain(): Product {
    return Product(
        id = product_id,
        name = product_name,
        price = product_price,
        subCategoryId = subcategory_id,
        categoryId = category_id,
        description = product_description ?: "",
        isBundled = product_is_bundled == 1L,
        quantity = product_quantity ?: 0L,
        imageUrl = product_image_url,
        serialNo = product_serial_no ?: ""
    )
}
