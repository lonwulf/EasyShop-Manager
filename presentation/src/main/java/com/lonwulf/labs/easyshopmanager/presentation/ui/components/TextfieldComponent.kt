package com.lonwulf.labs.easyshopmanager.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lonwulf.labs.easyshopmanager.presentation.domain.model.InputType
import com.lonwulf.labs.easyshopmanager.presentation.ui.theme.EasyShopManagerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputComponent(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    inputType: InputType = InputType.Text(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    enabled: Boolean = true,
    isSingleLine: Boolean = true,
    maxLines: Int = 1,
    fieldRequired: Boolean = false,
) {

    val isDropdown = inputType is InputType.Dropdown
    var dropdownExpanded by remember { mutableStateOf(false) }
    val keyboardOptions = when (inputType) {
        is InputType.Text -> KeyboardOptions(keyboardType = inputType.keyboardType, imeAction = ImeAction.Default)
        is InputType.Password -> KeyboardOptions(keyboardType = KeyboardType.Password)
        is InputType.Dropdown -> KeyboardOptions.Default
    }
    val visualTransformation = when {
        inputType is InputType.Password && !inputType.isVisible -> PasswordVisualTransformation()
        else -> VisualTransformation.None
    }
    val resolvedTrailingIcon: @Composable (() -> Unit)? = when (inputType) {
        is InputType.Dropdown -> ({ ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) })
        is InputType.Password -> trailingIcon?.let { icon ->
            { IconButton(onClick = inputType.onToggleVisibility) { icon() } }
        }

        else -> trailingIcon
    }

    val baseFieldModifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))

    val labelField: @Composable () -> Unit = {
        Row {
            Text(label)
            Text(
                text = " *",
                color = MaterialTheme.colorScheme.error
            )
        }
    }


    val textField: @Composable (modifier: Modifier) -> Unit = { finalModifier ->
        OutlinedTextField(
            modifier = finalModifier,
            value = value,
            onValueChange = if (isDropdown) ({}) else onValueChange,
            label = { if (fieldRequired) labelField() else Text(label) },
            placeholder = { Text(label) },
            leadingIcon = leadingIcon,
            trailingIcon = resolvedTrailingIcon,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            readOnly = isDropdown,
            singleLine = isSingleLine,
            isError = isError,
            supportingText = supportingText,
            colors = colors,
            enabled = enabled,
            maxLines = maxLines,
        )
    }

    if (inputType is InputType.Dropdown) {
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it },
            modifier = modifier
        ) {
            textField(
                baseFieldModifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            if (inputType.options.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    inputType.options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                inputType.onOptionSelected(option)
                                onValueChange(option)
                                dropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    } else {
        textField(baseFieldModifier)
    }
}

@Preview(showBackground = true)
@Composable
fun EmailTextInputComponentPreview() {
    EasyShopManagerTheme {
        var email by remember { mutableStateOf("") }
        TextInputComponent(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            inputType = InputType.Text(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlainTextInputComponentPreview() {
    EasyShopManagerTheme {
        var name by remember { mutableStateOf("") }
        TextInputComponent(
            value = name,
            onValueChange = { name = it },
            label = "Full Name",
            inputType = InputType.Text(keyboardType = KeyboardType.Text)
        )
    }
}

@Preview
@Composable
fun PhoneInputComponentPreview() {
    EasyShopManagerTheme {
        var phone by remember { mutableStateOf("") }
        TextInputComponent(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            inputType = InputType.Text(keyboardType = KeyboardType.Phone),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NumberInputComponentPreview() {
    EasyShopManagerTheme {
        var amount by remember { mutableStateOf("") }
        TextInputComponent(
            value = amount,
            onValueChange = { amount = it },
            label = "Amount (KES)",
            inputType = InputType.Text(keyboardType = KeyboardType.Decimal),
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
        )
    }
}

@Preview
@Composable
fun PasswordInputComponentPreview() {
    EasyShopManagerTheme {
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        TextInputComponent(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            inputType = InputType.Password(
                isVisible = passwordVisible,
                onToggleVisibility = { passwordVisible = !passwordVisible },
            ),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                )
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmPasswordInputComponentPreview() {
    EasyShopManagerTheme {
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var confirmPasswordVisible by remember { mutableStateOf(false) }
        TextInputComponent(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            inputType = InputType.Password(
                isVisible = confirmPasswordVisible,
                onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            ),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                Icon(
                    imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                )
            },
            isError = confirmPassword != password && confirmPassword.isNotEmpty(),
            supportingText = {
                if (confirmPassword != password && confirmPassword.isNotEmpty()) {
                    Text("Passwords do not match")
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DropdownInputComponentPreview() {
    EasyShopManagerTheme {
        var selectedCountry by remember { mutableStateOf("") }

        TextInputComponent(
            value = selectedCountry,
            onValueChange = { selectedCountry = it },
            label = "Country",
            inputType = InputType.Dropdown(
                options = listOf("Kenya", "Uganda", "Tanzania", "Ethiopia", "Rwanda"),
                onOptionSelected = { selectedCountry = it },
            ),
            leadingIcon = { Icon(Icons.Default.OutlinedFlag, contentDescription = null) },
        )
    }
}
