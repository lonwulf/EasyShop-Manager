package com.lonwulf.labs.easyshopmanager.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lonwulf.labs.easyshopmanager.auth.ui.components.ContinueWithEmailComponent
import com.lonwulf.labs.easyshopmanager.auth.ui.components.LabelTextFieldComponent
import com.lonwulf.labs.easyshopmanager.auth.ui.components.SocialButtonComponent
import com.lonwulf.labs.easyshopmanager.auth.ui.components.TermsAndPrivacyText
import com.lonwulf.labs.easyshopmanager.navigation.Destinations
import com.lonwulf.labs.easyshopmanager.navigation.NavComposable
import com.lonwulf.labs.easyshopmanager.presentation.domain.model.InputType
import com.lonwulf.labs.easyshopmanager.presentation.ui.components.ButtonComponent
import com.lonwulf.labs.easyshopmanager.presentation.ui.components.SpannableClickableText

class SignInScreenComposable : NavComposable {
    @Composable
    override fun Composable(navHostController: NavHostController) {
        SignInScreen(navHostController = navHostController)
    }
}

@Composable
fun SignInScreen(modifier: Modifier = Modifier, navHostController: NavHostController) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isChecked by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(7.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Sign in to your account",
            style = MaterialTheme.typography.bodyMedium
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SocialButtonComponent(
                modifier = Modifier.fillMaxWidth(),
                socialImg = Icons.Default.Facebook,
                onClick = {},
                text = "Facebook"
            )
            SocialButtonComponent(
                modifier = Modifier.fillMaxWidth(),
                socialImg = Icons.Default.Facebook,
                onClick = {},
                text = "Google"
            )
            SocialButtonComponent(
                modifier = Modifier.fillMaxWidth(),
                socialImg = Icons.Default.Facebook,
                onClick = {},
                text = "Apple"
            )
        }

        Spacer(Modifier.height(7.dp))

        ContinueWithEmailComponent()

        Spacer(Modifier.height(7.dp))

        LabelTextFieldComponent(
            title = "Email Address",
            inputType = InputType.Text(keyboardType = KeyboardType.Email),
            label = "you@domain.com",
            value = email,
            onValueChange = { email = it },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
        )

        LabelTextFieldComponent(
            title = "Password",
            inputType = InputType.Password(
                isVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
            ),
            label = "*************",
            value = password,
            onValueChange = { password = it },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                )
            },
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .toggleable(
                        value = isChecked,
                        onValueChange = { isChecked = it },
                        role = Role.Checkbox
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    colors = CheckboxDefaults.colors(),
                    checked = isChecked,
                    // Set to null so the Row handles the click events, preventing double-toggles
                    onCheckedChange = null
                )
                Text(
                    text = "Remember me",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            TextButton(onClick = {}) {
                Text(
                    text = "Forgot password?",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        ButtonComponent(text = "Sign In") {

        }

        SpannableClickableText(
            normalText = "Don't have an account? ",
            clickableText = "Sign Up",
            onClick = {
                navHostController.navigate(Destinations.SignUpScreen.route)
            }
        )

        Spacer(Modifier.height(7.dp))

        HorizontalDivider(
            Modifier
                .height(1.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )

        Spacer(Modifier.height(7.dp))

        TermsAndPrivacyText(onTermsClick = {}, onPrivacyClick = {})

        Spacer(Modifier.height(7.dp))

    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() = SignInScreen(navHostController = NavHostController(LocalContext.current))