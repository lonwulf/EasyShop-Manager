package com.lonwulf.labs.easyshopmanager.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
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
import com.lonwulf.labs.easyshopmanager.ui.theme.EasyShopManagerTheme


enum class ButtonType { ELEVATED, FILLED, OUTLINED, TEXT }

@Composable
fun ButtonComponent(
    modifier: Modifier = Modifier,
    text: String,
    buttonType: ButtonType = ButtonType.ELEVATED,
    colors: ButtonColors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    onClick: () -> Unit,
) {
    val buttonModifier = modifier.fillMaxWidth()
    val shape = RoundedCornerShape(8.dp)
    val textContent: @Composable () -> Unit = {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    when (buttonType) {
        ButtonType.ELEVATED -> ElevatedButton(
            modifier = buttonModifier,
            shape = shape,
            elevation = ButtonDefaults.buttonElevation(4.dp),
            colors = colors,
            onClick = onClick,
            content = { textContent() }
        )

        ButtonType.FILLED -> Button(
            modifier = buttonModifier,
            shape = shape,
            colors = colors,
            onClick = onClick,
            content = { textContent() }
        )

        ButtonType.OUTLINED -> OutlinedButton(
            modifier = buttonModifier,
            shape = shape,
            colors = colors,
            onClick = onClick,
            content = { textContent() }
        )

        ButtonType.TEXT -> TextButton(
            modifier = buttonModifier,
            shape = shape,
            colors = colors,
            onClick = onClick,
            content = { textContent() }
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
