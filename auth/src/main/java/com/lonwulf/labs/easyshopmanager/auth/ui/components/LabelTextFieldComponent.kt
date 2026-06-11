package com.lonwulf.labs.easyshopmanager.auth.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lonwulf.labs.easyshopmanager.presentation.domain.model.InputType
import com.lonwulf.labs.easyshopmanager.presentation.ui.components.TextInputComponent

@Composable
fun LabelTextFieldComponent(
    title: String,
    inputType: InputType,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )

        TextInputComponent(
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            value = value,
            onValueChange = onValueChange,
            label = label,
            inputType = inputType,
            isOutlined = false,
            fieldRequired = true,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            supportingText = supportingText
        )
    }
}