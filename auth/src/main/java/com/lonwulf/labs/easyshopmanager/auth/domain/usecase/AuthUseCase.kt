package com.lonwulf.labs.easyshopmanager.auth.domain.usecase

import com.lonwulf.labs.easyshopmanager.auth.domain.repository.IAuthRepository
import com.lonwulf.labs.easyshopmanager.core.domain.model.Resource
import com.lonwulf.labs.easyshopmanager.core.domain.model.UserPrefs
import com.lonwulf.labs.easyshopmanager.core.domain.repository.IDatastoreRepository
import com.lonwulf.labs.easyshopmanager.core.network.APIResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthUseCase(private val repository: IAuthRepository, private val dataStore: IDatastoreRepository) {
    fun signIn(email: String, password: String): Flow<Resource<Boolean>> = flow {
        when (val response = repository.signInUser(email, password)) {
            is APIResult.Loading -> emit(Resource.Loading)
            is APIResult.Success -> {
                dataStore.saveUserPrefsSettings(
                    UserPrefs(
                        fName = response.value?.fName ?: "",
                        lName = response.value?.lName ?: "",
                        email = response.value?.email ?: "",
                        userId = response.value?.id ?: "",
                        token = response.value?.token ?: "",
                        expiresIn = response.value?.expiresIn ?: 0
                    )
                )
                emit(Resource.Success(true))
            }

            is APIResult.Failure -> emit(Resource.Failure(response.errorMessage ?: "failed sign in", response.cause))
        }
    }

    suspend fun signUp(email: String, password: String, fName: String, lName: String) =
        repository.signUpUser(email, password, fName, lName)


}