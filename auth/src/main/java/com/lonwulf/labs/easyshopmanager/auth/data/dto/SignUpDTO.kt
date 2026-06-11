package com.lonwulf.labs.easyshopmanager.auth.data.dto

import com.lonwulf.labs.easyshopmanager.auth.domain.model.SignUpModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignUpDTO(
    val user: UserDto,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long,
    @SerialName("token_type") val tokenType: String,
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val fName: String,
    val lName: String,
    @SerialName("created_at")
    val createdAt: String,
)

fun SignUpDTO.toDomain(): SignUpModel = SignUpModel(
    id = this.user.id,
    email = this.user.email,
    fName = this.user.fName,
    lName = this.user.lName,
    createdAt = this.user.createdAt,
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    expiresIn = this.expiresIn,
    tokenType = this.tokenType
)
