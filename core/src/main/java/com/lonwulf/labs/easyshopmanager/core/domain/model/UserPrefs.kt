package com.lonwulf.labs.easyshopmanager.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPrefs(
    val fName:String = "",
    val lName:String = "",
    val email:String = "",
    val userId:String = "",
    val sessionToken:String = ""
)
