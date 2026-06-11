package com.lonwulf.labs.easyshopmanager.auth.data.dto

import com.lonwulf.labs.easyshopmanager.auth.domain.model.SignInModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInDTO(
    val fName: String?,
    val lName: String?,
    val email: String?,
    val token: String?,
    @SerialName("expires_in")
    val expiresIn: Int?,
    val id: String?,
)

fun SignInDTO.toDomain(): SignInModel = SignInModel(
    fName = this.fName,
    lName = this.lName,
    email = this.email,
    token = this.token,
    expiresIn = this.expiresIn,
    id = this.id,
)
