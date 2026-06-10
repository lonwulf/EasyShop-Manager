package com.lonwulf.labs.easyshopmanager.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class APIResponse<T>(
    val status:Int? = null,
    val message:String? = null,
    val data:T? = null
)