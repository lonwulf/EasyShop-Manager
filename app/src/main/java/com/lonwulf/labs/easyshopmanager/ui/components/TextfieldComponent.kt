package com.lonwulf.labs.easyshopmanager.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lonwulf.labs.easyshopmanager.domain.model.InputType

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
) {

    val isDropdown = inputType is InputType.Dropdown
    var dropdownExpanded by remember { mutableStateOf(false) }
    val keyboardOptions = when (inputType) {
        is InputType.Text -> KeyboardOptions(keyboardType = inputType.keyboardType)
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

    val baseFieldModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(5.dp))


    val textField: @Composable (modifier: Modifier) -> Unit = { finalModifier ->
        OutlinedTextField(
            modifier = finalModifier,
            value = value,
            onValueChange = if (isDropdown) ({}) else onValueChange,
            label = { Text(label) },
            placeholder = { Text(label) },
            leadingIcon = leadingIcon,
            trailingIcon = resolvedTrailingIcon,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            readOnly = isDropdown,
            singleLine = true,
            isError = isError,
            supportingText = supportingText,
            colors = colors,
            enabled = enabled,
        )
    }

    if (inputType is InputType.Dropdown) {
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it },
            modifier = modifier
        ) {
            textField(
                baseFieldModifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
