package com.lonwulf.labs.easyshopmanager.data.dto

import com.lonwulf.labs.easyshopmanager.core.domain.model.Brand
import kotlinx.serialization.Serializable

@Serializable
data class BrandDTO(val id: Long? = null, val name: String? = null, val origin: String? = null)

fun BrandDTO.toDomain(): Brand = Brand(id = this.id ?: 0L, name = this.name ?: "", origin = this.origin)