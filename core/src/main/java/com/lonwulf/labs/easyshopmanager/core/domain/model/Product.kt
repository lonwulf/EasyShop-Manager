package com.lonwulf.labs.easyshopmanager.core.domain.model

import com.lonwulf.labs.easyshopmanager.db.GetProductById
import com.lonwulf.labs.easyshopmanager.db.ProductWithRelations
import com.lonwulf.labs.easyshopmanager.db.SelectProductsByBrand
import com.lonwulf.labs.easyshopmanager.db.SelectProductsWithCategory

data class Product(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val categoryId: Long,
    val subCategoryId: Long,
    val price: Double,
    val buyingPrice: Double,
    val isBundled: Boolean? = false,
    val quantity: Long? = 0L,
    val imageUrl: String? = null,
    val serialNo: String? = null,
    val brand: String? = null,
    val vendor: String? = null
)


fun ProductWithRelations.toDomain(): Product =
    Product(
        id = id,
        name = name,
        description = description,
        categoryId = category_id,
        subCategoryId = sub_category_id,
        price = price,
        buyingPrice = buying_price,
        isBundled = is_bundled == 1L,
        quantity = quantity,
        imageUrl = image_url,
        serialNo = serial_no,
        brand = brand_name,
        vendor = vendor
    )

fun SelectProductsWithCategory.toDomain(): Product {
    return Product(
        id = product_id,
        name = product_name,
        price = product_price,
        buyingPrice = product_buying_price,
        subCategoryId = subcategory_id,
        categoryId = category_id,
        description = product_description,
        isBundled = product_is_bundled == 1L,
        quantity = product_quantity,
        imageUrl = product_image_url,
        serialNo = product_serial_no,
        brand = brand_name,
        vendor = product_vendor
    )
}

fun GetProductById.toDomain(): Product =
    Product(
        id = id,
        name = name,
        description = description,
        categoryId = category_id,
        subCategoryId = sub_category_id,
        price = price,
        buyingPrice = buying_price,
        isBundled = is_bundled == 1L,
        quantity = quantity,
        imageUrl = image_url,
        serialNo = serial_no,
        brand = brand_name,
        vendor = vendor
    )

fun SelectProductsByBrand.toDomain(): Product =
    Product(
        id = id,
        name = name,
        description = description,
        categoryId = category_id,
        subCategoryId = sub_category_id,
        price = price,
        buyingPrice = buying_price,
        isBundled = is_bundled == 1L,
        quantity = quantity,
        imageUrl = image_url,
        serialNo = serial_no,
        brand = brand_name,
        vendor = vendor
    )
