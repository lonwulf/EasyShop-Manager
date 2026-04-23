package com.lonwulf.labs.easyshopmanager.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val fName:String = "",
    val lName:String = "",
    val email:String = "",
    val userId:String = "",
    val sessionToken:String = ""
)