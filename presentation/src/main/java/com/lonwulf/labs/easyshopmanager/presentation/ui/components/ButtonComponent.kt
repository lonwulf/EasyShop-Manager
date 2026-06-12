package com.lonwulf.labs.easyshopmanager.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lonwulf.labs.easyshopmanager.presentation.ui.theme.EasyShopManagerTheme


enum class ButtonType { ELEVATED, FILLED, OUTLINED, TEXT }

@Composable
fun ButtonComponent(
    modifier: Modifier = Modifier,
    text: String,
    buttonType: ButtonType = ButtonType.ELEVATED,
    colors: ButtonColors? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val buttonModifier = modifier.fillMaxWidth()
    val shape = RoundedCornerShape(8.dp)

    val resolvedColors = colors ?: when (buttonType) {
        ButtonType.ELEVATED -> ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ButtonType.FILLED -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ButtonType.OUTLINED -> ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ButtonType.TEXT -> ButtonDefaults.textButtonColors()
    }

    val buttonContent: @Composable () -> Unit = {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        // 3. Trailing Icon
        if (trailingIcon != null) {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            trailingIcon()
        }
    }

    when (buttonType) {
        ButtonType.ELEVATED -> ElevatedButton(
            modifier = buttonModifier,
            shape = shape,
            elevation = ButtonDefaults.buttonElevation(4.dp),
            colors = resolvedColors,
            onClick = onClick,
            content = { buttonContent() }
        )

        ButtonType.FILLED -> Button(
            modifier = buttonModifier,
            shape = shape,
            colors = resolvedColors,
            onClick = onClick,
            content = { buttonContent() }
        )

        ButtonType.OUTLINED -> OutlinedButton(
            modifier = buttonModifier,
            shape = shape,
            colors = resolvedColors,
            onClick = onClick,
            content = { buttonContent() }
        )

        ButtonType.TEXT -> TextButton(
            modifier = buttonModifier,
            shape = shape,
            colors = resolvedColors,
            onClick = onClick,
            content = { buttonContent() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonType1() {
    EasyShopManagerTheme {
        ButtonComponent(
            text = "Submit",
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonType2() {
    ButtonComponent(
        text = "Submit",
        buttonType = ButtonType.FILLED,
        onClick = { }
    )
}

@Preview(showBackground = true)
@Composable
fun ButtonType3() {
    ButtonComponent(
        text = "Cancel",
        buttonType = ButtonType.OUTLINED,
        colors = ButtonDefaults.outlinedButtonColors(),
        onClick = { }
    )
}

@Preview(showBackground = true)
@Composable
fun ButtonType4() {
    ButtonComponent(
        text = "Skip",
        buttonType = ButtonType.TEXT,
        colors = ButtonDefaults.textButtonColors(),
        onClick = { }
    )
}
