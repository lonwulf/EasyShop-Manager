package com.lonwulf.labs.easyshopmanager.auth.domain.repository

import com.lonwulf.labs.easyshopmanager.auth.data.dto.SignInDTO
import com.lonwulf.labs.easyshopmanager.auth.data.dto.SignUpDTO
import com.lonwulf.labs.easyshopmanager.auth.domain.model.SignInModel
import com.lonwulf.labs.easyshopmanager.auth.domain.model.SignUpModel
import com.lonwulf.labs.easyshopmanager.core.network.APIResult

interface IAuthRepository {

    suspend fun signInUser(email: String, password: String): APIResult<SignInModel?>
    suspend fun signUpUser(email: String, password: String, fName: String, lName: String): APIResult<SignUpModel?>
}