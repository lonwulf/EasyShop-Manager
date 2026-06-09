package com.lonwulf.labs.easyshopmanager.core.domain.model

data class Brand(val id: Long, val name: String, val origin: String? = null)

fun Brand.toEntity(): com.lonwulf.labs.easyshopmanager.db.Brand = com.lonwulf.labs.easyshopmanager.db.Brand(id, name, origin)

fun com.lonwulf.labs.easyshopmanager.db.Brand.toDomain(): Brand = Brand(id, name, origin)
