package com.lonwulf.labs.easyshopmanager.domain.model

import androidx.compose.ui.text.input.KeyboardType

sealed class InputType {

    /** Plain text, email, number, phone, etc. */
    data class Text(
        val keyboardType: KeyboardType = KeyboardType.Text,
    ) : InputType()

    /** Password field with a show/hide toggle. */
    data class Password(
        val isVisible: Boolean = false,
        val onToggleVisibility: () -> Unit,
    ) : InputType()

    // Exposed dropdown
    data class Dropdown(
        val options: List<String>,
        val onOptionSelected: (String) -> Unit = {},
    ) : InputType()
}