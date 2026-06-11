package com.lonwulf.labs.easyshopmanager.auth.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lonwulf.labs.easyshopmanager.auth.domain.usecase.AuthUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AuthViewModel(private val authUseCase: AuthUseCase) : ViewModel() {

    fun signIn(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        authUseCase.signIn(email, password)
            .onStart { }
            .flowOn(Dispatchers.IO)
            .collect {

            }

    }

    fun signUp(email: String, password: String, fName: String, lName: String) = viewModelScope.launch(Dispatchers.IO) {
        authUseCase.signUp(email, password, fName, lName)
            .onStart { }
            .flowOn(Dispatchers.IO)
            .collect {

            }
    }

    fun isSignInCredentialsValid(email: String, password: String): ValidationResult {
        val errors = mutableMapOf<SignUpField, String>()

        when {
            email.isBlank() -> errors[SignUpField.EMAIL] = "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errors[SignUpField.EMAIL] =
                "Enter a valid email address"
        }
        when {
            password.isBlank() -> errors[SignUpField.PASSWORD] =
                "Password is required"
        }
        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(errors)
    }

    fun isSignUpCredentialsValid(
        email: String,
        password: String,
        fName: String,
        lName: String,
    ): ValidationResult {
        val errors = mutableMapOf<SignUpField, String>()

        when {
            email.isBlank() -> errors[SignUpField.EMAIL] = "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errors[SignUpField.EMAIL] =
                "Enter a valid email address"
        }

        when {
            password.isBlank() -> errors[SignUpField.PASSWORD] = "Password is required"
            password.length < 8 -> errors[SignUpField.PASSWORD] = "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> errors[SignUpField.PASSWORD] =
                "Password must contain at least one uppercase letter"

            !password.any { it.isDigit() } -> errors[SignUpField.PASSWORD] = "Password must contain at least one number"
            !password.any { !it.isLetterOrDigit() } -> errors[SignUpField.PASSWORD] =
                "Password must contain at least one special character"
        }

        when {
            fName.isBlank() -> errors[SignUpField.FIRST_NAME] = "First name is required"
            fName.length < 2 -> errors[SignUpField.FIRST_NAME] = "First name must be at least 2 characters"
            !fName.all { it.isLetter() || it == '-' || it == '\'' } -> errors[SignUpField.FIRST_NAME] =
                "First name contains invalid characters"
        }

        when {
            lName.isBlank() -> errors[SignUpField.LAST_NAME] = "Last name is required"
            lName.length < 2 -> errors[SignUpField.LAST_NAME] = "Last name must be at least 2 characters"
            !lName.all { it.isLetter() || it == '-' || it == '\'' } -> errors[SignUpField.LAST_NAME] =
                "Last name contains invalid characters"
        }

        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(errors)
    }

    fun extractNamesFromFullName(fullName: String): Pair<String, String> {
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) return Pair("", "")

        val parts = trimmedName.split("\\s+".toRegex())
        return when (parts.size) {
            1 -> Pair(parts[0], "")
            2 -> Pair(parts[0], parts[1])
            else -> Pair(parts.first(), parts.drop(1).joinToString(" "))
        }
    }
}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Failure(val errors: Map<SignUpField, String>) : ValidationResult()
}

enum class SignUpField {
    EMAIL, PASSWORD, FIRST_NAME, LAST_NAME
}